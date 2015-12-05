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
package nz.co.fortytwo.signalk.model;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;

import com.google.common.eventbus.EventBus;

public interface SignalKModel{
		
	/**
	 * Get a value from the Model
	 */
	public abstract Object get(String key);

	/**
	 * Return a subtree from the Model - the tree is read-only, but is live
	 * and will be updated as the Model changes.
	 * @param key the key to retrieve - for example, "vessels.SignalKConstants.self" will return
	 * a subtree that may contain keys beginning "navigation", "environment" and so on.
	 */
	public abstract NavigableSet<String> getTree(String key);

	/**
	 * Release the write lock, update the revision (assuming the model
	 * has changed) and call {@link #modelChanged}
	 */
	//public abstract boolean unlock();

	public abstract EventBus getEventBus();

	/**
	 * Return the full set of keys from this Model. The returned set
	 * is read-only and guaranteed to be the full set at the time this method is called,
	 * but as the model may be updated in another thread immediately after it's accuracy
	 * is no longer guaranteed after that. It can be iterated over without synchronization.
	 */
	public abstract NavigableSet<String> getKeys();

	/**
	 * Return the underlying data model. Note this is the live object,
	 * and should not be read without a {@link #readLock} being acquired, or
	 * modified at all.
	 */
	public abstract SortedMap<String, Object> getData();

	/**
	 * Add all values to the model
	 * @param map
	 * @return
	 */
	public boolean putAll(SortedMap<String, Object> map);
	/**
	 * Generic put that accepts only null, boolean, Number, String
	 * @param key
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean put(String key, Object value) throws IllegalArgumentException;
	
	public boolean put(String key, Object value, String source) throws IllegalArgumentException;
	public boolean put(String key, Object value, String source, String timestamp) throws IllegalArgumentException;

	/**
	 * Return a submap from the Model - the tree is read-only, but is live
	 * and will be updated as the Model changes.
	 * @param key the key to retrieve - for example, "vessels.SignalKConstants.self" will return
	 * a submap that may contain keys beginning "navigation", "environment" and so on.
	 */
	 
	public NavigableMap<String, Object> getSubMap(String key);

	public Object getValue(String key);

	/**
	 * Same as put, but it adds the suffix '.value' to the key
	 * @param string
	 * @param value
	 * @return
	 */
	public boolean putValue(String string, Object value);

	/**
	 * Gets the full data map. Use with care, it holds the config data too.
	 * @return
	 */
	SortedMap<String, Object> getFullData();

}
