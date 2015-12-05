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
import static nz.co.fortytwo.signalk.util.SignalKConstants.*;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.Constants;
import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;

/**
 * <p>
 * A thread-safe datamodel. Objects are stored with hierarchical keys, eg "a.b"
 * or "a.b.c", and a node in the tree can be a leaf or an intermediate, not both.
 * Nodes are always stored alphabetically.  Objects can be inserted or deleted
 * on any thread  or for the whole model or a subtree
 * of it without locking and without needing to synchronized on the returned tree.
 * </p><pre>

 * model.put("vessels.SignalKConstants.self.navigation.position.latitude", 57.9);
 * model.put("vessels.SignalKConstants.self.navigation.position.longitude", 17.2);
 * model.put("vessels.SignalKConstants.self.navigation.position.source", "gps");
 * model.put("vessels.SignalKConstants.self.navigation.position.teimstamp", model.timestamp());

 * </pre>
 * <p>
 * Changes to the model are notified on the Guava EventBus. To listen for changes 
 * obtain the event bus (getEventBus()) and register for PathEvent
 * </p>
 */
public class SignalKModelImpl implements SignalKModel {
    
	private static Logger logger = Logger.getLogger(SignalKModelImpl.class);
    private final char separator;
    private final NavigableMap<String,Object> root;
    
    private int nextrevision;

  	private EventBus eventBus = new EventBus();
    
  	 /**
     * Create a new Model
     */
    public SignalKModelImpl() {
        this.separator = '.';
        root = new ConcurrentSkipListMap<String,Object>();
    }
    
    /**
     * Create a new model from the provided sublist.
     * @param root
     */
    public SignalKModelImpl(NavigableMap<String,Object> root) {
        this.separator = '.';
        this.root = new ConcurrentSkipListMap<String,Object>(root);
    }
    
    
    /**
     * Return the hierarchy separator
     */
    public char getSeparator() {
        return separator;
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
        //meta.zones array
        if (!value.equals(root.put(key, value))) {
        	if(logger.isDebugEnabled())logger.debug("doPut "+key+"="+value);
        	if(!key.endsWith(dot+source)&& !key.endsWith(dot+timestamp)&&!key.contains(dot+source+dot)){
        		eventBus.post(new PathEvent(key, nextrevision, PathEvent.EventType.ADD));
        	}
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
    	key = fixSelfKey(key);
    	if(value == null){
    		return doDelete(key);
		}
    	if(value instanceof Boolean || value instanceof Number || value instanceof String){
    		if(logger.isDebugEnabled())logger.debug("Put "+key+"="+value);
    		return doPut(key, value);
    	}
    	if(value instanceof Json && ((Json)value).isArray() ){
    		if(logger.isDebugEnabled())logger.debug("Put "+key+"="+value);
    		return doPut(key, value);
    	}
    	throw new IllegalArgumentException("Must be String, Number,Boolean or null : "+value.getClass()+":"+value);
    }

    private String fixSelfKey(String key) {
    
		return Util.fixSelfKey(key);
	}

	@Override
	public boolean put(String key, Object value, String source) throws IllegalArgumentException {
    	key = fixSelfKey(key);
    	if(source==null)return (doPut(key, value));
		return (doPut(key+".value", value)&& doPut(key+".source", source));
	}

	@Override
	public boolean put(String key, Object value, String source, String timestamp) throws IllegalArgumentException {
		key = fixSelfKey(key);
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
    	key = fixSelfKey(key);
    	return nullFix(root.get(key));
    }
    
    /**
     * ConcurrentSkipList cant store nulls so we store "null". Fix that here
     * @param object
     * @return
     */
    private Object nullFix(Object object) {
		if("null".equals(object))return null;
		return object;
	}

	/* (non-Javadoc)
   	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#get(java.lang.String)
   	 */
       @Override
   	public Object getValue(String key) {
    	   key = fixSelfKey(key);
               return nullFix(root.get(key+".value"));
       }

    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getTree(java.lang.String)
	 */
    @Override
	public NavigableSet<String> getTree(String key) {
    	key = fixSelfKey(key);
         return getKeys().subSet(key, true, key+".\uFFFD", true);
    }
    
    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getTree(java.lang.String)
	 */
    @Override
	public NavigableMap<String, Object> getSubMap(String key) {
    	key = fixSelfKey(key);
            return root.subMap(key, true, key+".\uFFFD", true);
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
        return getSubMap(vessels);
    }
    
    /* (non-Javadoc)
	 * @see nz.co.fortytwo.signalk.model.impl.SignalKModel#getData()
	 */
    @Override
	public SortedMap<String,Object> getFullData() {
        return root;
    }

    public String toString() {
            return root.toString();
    }

	@Override
	public boolean putAll(SortedMap<String, Object> map) {
		boolean success = true;
		for(Entry<String, Object> entry: map.entrySet()){
			if(logger.isDebugEnabled())logger.debug("Adding "+entry.getKey()+"="+entry.getValue());
			boolean s = put(entry.getKey(),entry.getValue());
			success = success && s;
		}
		if(logger.isDebugEnabled())logger.debug("putAll done: "+this);
		return success;
	}

	@Override
	public boolean putValue(String key, Object value) {
		key = fixSelfKey(key);
		return put(key+".value", value);
	}

	

}

