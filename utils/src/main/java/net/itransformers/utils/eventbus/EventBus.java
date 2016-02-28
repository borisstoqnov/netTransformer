/*
 * EventBus.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.utils.eventbus;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.*;

public class EventBus {
    private static Map<String, EventBus> eventBuses = new HashMap<String,EventBus>();
    private Map<String, RegistryEntry> registry = new HashMap<String, RegistryEntry>();

    private EventBus() {
    }

    public static EventBus getInstance(String name){
        if (eventBuses.get(name) == null) {
            eventBuses.put(name, new EventBus());
        }
        return eventBuses.get(name);
    }

    public void registerEventListener(String name, EventListener eventListener, EventFilter eventFilter){
        registry.put(name, new RegistryEntry(eventListener,eventFilter));
    }
    public void unregisterEventListener(String name){
        registry.remove(name);
    }

    public void fireEvent(Event event){
        for (String name : registry.keySet()) {
            RegistryEntry entry = registry.get(name);
            EventFilter filter = entry.getEventFilter();
            if (!filter.filterEvent(event)) {
                entry.getEventListener().handleEvent(event);
            }
        }
    }

    public Set<String> getListenerNames() {
        return registry.keySet();
    }
    public Map<String, EventListener> getListeners(){
        HashMap<String, EventListener> result = new HashMap<String, EventListener>();
        for (String key : registry.keySet()) {
            result.put(key, registry.get(key).getEventListener());
        }
        return result;
    }

    class RegistryEntry {
        private EventListener eventListener;
        private EventFilter eventFilter;

        RegistryEntry(EventListener eventListener, EventFilter eventFilter) {
            this.eventListener = eventListener;
            this.eventFilter = eventFilter;
        }

        public EventListener getEventListener() {
            return eventListener;
        }

        public EventFilter getEventFilter() {
            return eventFilter;
        }
    }
}
