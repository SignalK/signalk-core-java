/*
 *
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nz.co.fortytwo.signalk.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;

import com.google.common.eventbus.EventBus;

/**
 * <p>
 * A thread-safe datamodel. Objects are stored with hierarchical keys, eg "a.b"
 * or "a.b.c", and a node in the tree can be a leaf or an intermediate, not both.
 * Nodes are always stored alphabetically.  Objects can be inserted or deleted
 * on any thread so long as a lock is acquired, and any thread can retrieve the
 * list of keys modified for that revision, or for the whole model or a subtree
 * of it without locking and without needing to synchronized on the returned tree.
 * </p><p>
 * To update, the model lock should be acquired, then fields set, and finally the
 * model unlocked, e.g.
 * </p><pre>
 * model.lock();
 * model.put("vessels.self.navigation.position.latitude", 57.9);
 * model.put("vessels.self.navigation.position.longitude", 17.2);
 * model.put("vessels.self.navigation.position.source", "gps");
 * model.put("vessels.self.navigation.position.teimstamp", model.timestamp());
 * model.unlock();
 * </pre>
 * <p>
 * On unlock, provided the model has been updated the revision will be
 * incremented. Any objects that want to be notified of this change should
 * wait on the model from another thread; either directly, or by calling {@link #watch}.
 * When notified they can retrieve the current state of the model by calling
 * {@link #getRevision}, and {@link #getRevisionKeys}, or they can get the same
 * data from {@link ModelEvent} if they called watch. For example:
 * </p><pre>
 * ModelEvent event = model.watch(1000, watchtest);
 * if (event == null) {
 *    // 1000ms without a matching event reached
 * } else {
 *    // event is now an event that was "watched" by watchtest
 * }
 * </pre>
 */
public class SignalKModelImpl implements SignalKModel {
    
    private final char separator;
    private final NavigableMap<String,Object> root;
    private final int numrevisions;
    private final NavigableSet<String>[] mark;
    private final DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean alive;
    private volatile int revision;
    private int nextrevision;
    private long touch;
    private String formattouch, revisionkey;
  //private Json this;
  	private EventBus eventBus = new EventBus();
    
  	 /**
     * Create a new Model
     * @param numrevisions = 100, the number of revisions to record a list of changed keys. The only
     * storage overhead here is one Set per revision, so more is not expensive.
     * @param separator = '.', the hierarchy separator, eg '.' or '/'
     */
    public SignalKModelImpl() {
    	this.numrevisions = 1000;
        this.separator = '.';
        root = new ConcurrentSkipListMap<String,Object>();
        mark = new NavigableSet[numrevisions];
        alive = true;
    }
    
    /**
     * Create a new model from the provided sublist.
     * @param root
     */
    public SignalKModelImpl(NavigableMap<String,Object> root) {
    	this.numrevisions = 1000;
        this.separator = '.';
        this.root = new ConcurrentSkipListMap<String,Object>(root);
        mark = new NavigableSet[numrevisions];
        alive = true;
    }
    
    /**
     * Create a new Model
     * @param numrevisions the number of revisions to record a list of changed keys. The only
     * storage overhead here is one Set per revision, so more is not expensive.
     * @param separator the hierarchy separator, eg '.' or '/'
     */
    public SignalKModelImpl(int numrevisions, char separator) {
        this.numrevisions = numrevisions;
        this.separator = separator;
        root = new ConcurrentSkipListMap<String,Object>();
        mark = new NavigableSet[numrevisions];
        alive = true;
    }

    /**
     * Return the hierarchy separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Set the key against which to store the current revision number in the
     * model, or null to not store it
     */
    public void setRevisionKey(String key) {
        this.revisionkey = key;
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#lock()
	 */
    @Override
	public void lock() {
        lock.writeLock().lock();
        nextrevision++;
        // Create a new one here because it's possible for a slow thread
        // still to have a handle on an old one. This will eliminate
        // any concurrency problems.
        setmark(nextrevision % numrevisions, new TreeSet<String>());
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#readLock()
	 */
    @Override
	public void readLock() {
        lock.readLock().lock();
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#readUnlock()
	 */
    @Override
	public void readUnlock() {
        lock.readLock().unlock();
    }

    /**
     * Return an ISO-8601 timestamp for the current time. This method
     * caches its return value so can be called whenever a timestamp
     * is required.
     */
    public String timestamp() {
        long s = System.currentTimeMillis() / 1000;
        if (touch != s) {
            formattouch = iso8601.format(new Date());
            touch = s;
        }
        return formattouch;
    }

    private boolean doPut(String key, Object value) {
        // If value = "aa.bb.cc", fail if map contains "aa.bb" or "aa.bb.cc.dd"
        String othkey = root.lowerKey(key);
        if (othkey != null && key.startsWith(othkey) && key.charAt(othkey.length()) == separator) {
            throw new IllegalArgumentException("Can't insert key \""+key+"\" into Model containing \""+othkey+"\"");
        }
        othkey = root.higherKey(key);
        if (othkey != null && othkey.startsWith(key) && othkey.charAt(key.length()) == separator) {
            throw new IllegalArgumentException("Can't insert key \""+key+"\" into Model containing \""+othkey+"\"");
        }
        if (!value.equals(root.put(key, value))) {
        	eventBus.post(new PathEvent(key, nextrevision, PathEvent.EventType.ADD));
            //mark(key);
            return true;
        } else {
            return false;
        }
    }

    private boolean doDelete(String key) {
        NavigableMap<String,Object> map = root.tailMap(key, true);
        boolean found = false;
        for (Iterator<String> i = map.keySet().iterator();i.hasNext();) {
            String mapkey = i.next();
            if (mapkey.startsWith(key) && (mapkey.length() == key.length() || mapkey.charAt(key.length()) == separator)) {
            	eventBus.post(new PathEvent(mapkey, nextrevision ,PathEvent.EventType.DEL));
                //mark(mapkey);
                i.remove();
                found = true;
            } else {
                break;
            }
        }
        return found;
    }

 
    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#put(java.lang.String, boolean)
	 */
    @Override
	public boolean put(String key, Object value) throws IllegalArgumentException{
    	if(value == null){
    		return doDelete(key);
		}
    	if(value instanceof Boolean || value instanceof Number || value instanceof String){
    		return doPut(key, value);
    	}
    	throw new IllegalArgumentException("Must be String, Number,Boolean or null : "+value);
    }

    @Override
	public boolean put(String key, Object value, String source) throws IllegalArgumentException {
    	if(source==null)return (doPut(key, value));
		return (doPut(key+".value", value)&& doPut(key+".source", source));
	}

	@Override
	public boolean put(String key, Object value, String source, String timestamp) throws IllegalArgumentException {
		if(source!=null&& timestamp!=null)return (doPut(key+".value", value)&& doPut(key+".source", source)&& doPut(key+".timestamp", timestamp));
		if(source!=null&& timestamp==null)return (doPut(key+".value", value)&& doPut(key+".source", source));
		if(source==null&& timestamp!=null)return (doPut(key+".value", value)&& doPut(key+".timestamp", timestamp));
		return (doPut(key+".value", value));
	}
    

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#get(java.lang.String)
	 */
    @Override
	public Object get(String key) {
        try {
            lock.readLock().lock();
            return root.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /* (non-Javadoc)
   	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#get(java.lang.String)
   	 */
       @Override
   	public Object getValue(String key) {
           try {
               lock.readLock().lock();
               return root.get(key+".value");
           } finally {
               lock.readLock().unlock();
           }
       }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getTree(java.lang.String)
	 */
    @Override
	public NavigableSet<String> getTree(String key) {
        try {
            lock.readLock().lock();
            //return Collections.unmodifiableNavigableSet(getKeys().subSet(key+".", true, key+".\uFFFD", true));
            return getKeys().subSet(key+".", true, key+".\uFFFD", true);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getTree(java.lang.String)
	 */
    @Override
	public NavigableMap<String, Object> getSubMap(String key) {
        try {
            lock.readLock().lock();
            //return Collections.unmodifiableNavigableSet(getKeys().subSet(key+".", true, key+".\uFFFD", true));
            return root.subMap(key+".", true, key+".\uFFFD", true);
            
        } finally {
            lock.readLock().unlock();
        }
    }

  

    @SuppressWarnings("unchecked")
    private NavigableSet<String> getmark(int i) {
        return mark[i];
    }

    @SuppressWarnings("unchecked")
    private void setmark(int i, NavigableSet<String> v) {
        mark[i] = v;
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#unlock()
	 */
    @Override
	public boolean unlock() {
        NavigableSet<String> m = getmark(nextrevision % numrevisions);
        if (m.isEmpty()) {
            lock.writeLock().unlock();
            return false;
        } else {
            revision = nextrevision; 
            if (revisionkey != null) {
                put(revisionkey, revision);
            }
            lock.writeLock().unlock();
            //setmark(revision % numrevisions, m = Collections.unmodifiableNavigableSet(m));
            setmark(revision % numrevisions, m );
            modelChanged();
            return true;
        }
    }

    /**
     * Called when the Model has changed, the default implementation
     * will notify any object that called Object.wait on this object.
     */
    protected void modelChanged() {
        //event = new ModelEvent(this, revision, getRevisionKeys(revision));
        synchronized(this) {
            notifyAll();
        }
    }

  
    /**
     * Close the model - all this will do is interrupt any Objects waiting on this
     */
    public synchronized void close() {
        alive = false;
        notifyAll();
    }
    
    /**
     * Return true if the model is alive (i.e. not closed).
     */
    public synchronized boolean isAlive() {
        return alive;
    }


    /**
     * Return the current revision - this increased by one every time
     * {@link #unlock} is called after changes have been made.
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Return the oldest revision number that can be passed into {@link #getRevisionKeys}
     */
    public int getOldestRevision() {
        return Math.max(0, nextrevision - numrevisions + 1);
    }

    /** 
     * Return a list of all keys modified between the specified revision
     * and the current revision, inclusive. If the specified revision has
     * expired, this method returns null - it can't be determined what has
     * changed between the specified revision and now.
     * Returned set is read-only and is guaranteed to remain unchanged
     * during its lifetime - there is no need to synchronize.
     */
    public NavigableSet<String> getRevisionKeys(int revision) {
        if (revision < getOldestRevision()) {
            return null;
        } else if (revision == this.revision) {
            return getmark(revision % numrevisions);
        } else {
            NavigableSet<String> s = new TreeSet<String>();
            for (int i=revision;i<=this.revision;i++) {
                Set<String> ss = getmark(i % numrevisions);
                if (ss != null) {
                    s.addAll(ss);
                }
            }
            //return Collections.unmodifiableNavigableSet(s);     // Java 8 method
            return s;
        }
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getEventBus()
	 */
    @Override
	public EventBus getEventBus() {
		return eventBus;
	}
    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getKeys()
	 */
    @Override
	public NavigableSet<String> getKeys() {
        //return Collections.unmodifiableNavigableSet(root.navigableKeySet());      // Java 8 method
    	return root.navigableKeySet();
    }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getData()
	 */
    @Override
	public SortedMap<String,Object> getData() {
        return root;
    }

    public String toString() {
        try {
            lock.readLock().lock();
            return root.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

	@Override
	public boolean putAll(SortedMap<String, Object> map) {
		root.putAll(map);
		return true;
	}

	@Override
	public boolean putValue(String key, Object value) {
		return put(key+".value", value);
	}

	

    /*
    public static void main(String[] args) throws Exception {
        Appendable out = System.out;

        Model model = new Model(5, '.');
        model.lock();
        model.put("navigation.heading.magnetic", 29.1);
        model.unlock();
        model.lock();
        model.put("navigation.heading.magnetic2", "b");
        model.unlock();
        Set x = model.getTree("navigation.heading");
        System.out.println(x);
        model.lock();
        model.put("navigation.depth", "c");
        model.unlock();
        model.lock();
        model.put("navigation.wind.angle", "c");
        model.put("navigation.wind.direction", "c");
        model.unlock();
        model.lock();
        model.put("navigation.heading.true", null);
        model.unlock();
        model.lock();
        model.put("navigation.heading.magnetic", null);
        model.unlock();
        System.out.println(x);
        model.lock();
        model.put("navigation", null);
        model.unlock();
    }
    */


}

