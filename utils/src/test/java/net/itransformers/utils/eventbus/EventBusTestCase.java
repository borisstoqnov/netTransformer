///*
// * iTransformer is an open source tool able to discover and transform
// *  IP network infrastructures.
// *  Copyright (C) 2012  http://itransformers.net
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package net.itransformers.utils.eventbus;/*
// * iTransformer is an open source tool able to discover IP networks
// * and to perform dynamic data data population into a xml based inventory system.
// * Copyright (C) 2010  http://itransformers.net
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//import junit.framework.Assert;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class EventBusTestCase {
//    @Test
//    public void testEventBusFiltered() {
//        EventBus eventBus = EventBus.getInstance("DiscoveryEventBus");
//        final boolean[] handledEvent = new boolean[1];
//        handledEvent[0] = false;
//        eventBus.registerEventListener("listener1",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[0] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return true;
//                    }
//                }
//        );
//        eventBus.fireEvent(new Event());
//        Assert.assertFalse(handledEvent[0]);
//    }
//    @Test
//    public void testEventBusNotFiltered() {
//        EventBus eventBus = EventBus.getInstance("DiscoveryEventBus");
//        final boolean[] handledEvent = new boolean[1];
//        handledEvent[0] = false;
//        eventBus.registerEventListener("listener",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[0] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return false;
//                    }
//                }
//        );
//        eventBus.fireEvent(new Event());
//        Assert.assertTrue(handledEvent[0]);
//    }
//    @Test
//    public void testEventBusTwoListenersNotFiltered() {
//        EventBus eventBus = EventBus.getInstance("DiscoveryEventBus");
//        final boolean[] handledEvent = new boolean[2];
//        handledEvent[0] = false;
//        handledEvent[1] = false;
//        eventBus.registerEventListener("listener1",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[0] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return false;
//                    }
//                }
//        );
//        eventBus.registerEventListener("listener2",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[1] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return false;
//                    }
//                }
//        );
//        eventBus.fireEvent(new Event());
//        Assert.assertTrue(handledEvent[0]);
//        Assert.assertTrue(handledEvent[1]);
//    }
//    @Test
//    public void testEventBusTwoListenersOneFiltered() {
//        EventBus eventBus = EventBus.getInstance("DiscoveryEventBus");
//        final boolean[] handledEvent = new boolean[2];
//        handledEvent[0] = false;
//        handledEvent[1] = false;
//        eventBus.registerEventListener("listener1",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[0] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return true;
//                    }
//                }
//        );
//        eventBus.registerEventListener("listener2",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[1] = true;
//                    }
//                },
//                new EventFilter() {
//                    @Override
//                    public boolean filterEvent(Event event) {
//                        return false;
//                    }
//                }
//        );
//        eventBus.fireEvent(new Event());
//        Assert.assertFalse(handledEvent[0]);
//        Assert.assertTrue(handledEvent[1]);
//    }
//    @Test
//    public void testEventBusTwoListenersDefaultFilter() {
//        EventBus eventBus = EventBus.getInstance("DiscoveryEventBus");
//
//        Map<String, String> eventAttributes1 = new HashMap<String, String>();
//        eventAttributes1.put("param11","value11");
//        eventAttributes1.put("param12","value12");
//        Map<String, String> eventAttributes2 = new HashMap<String, String>();
//        eventAttributes2.put("param21","value21");
//        eventAttributes2.put("param22","value22");
//
//        final boolean[] handledEvent = new boolean[2];
//        handledEvent[0] = false;
//        handledEvent[1] = false;
//        eventBus.registerEventListener("listener1",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[0] = true;
//                    }
//                },
//                new DefaultEventFilter(eventAttributes1)
//        );
//        eventBus.registerEventListener("listener2",
//                new EventListener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        handledEvent[1] = true;
//                    }
//                },
//                new DefaultEventFilter(eventAttributes2)
//        );
//        Event event = new Event();
//        event.setAttribute("param11","value11");
//        event.setAttribute("param21","value21");
//        event.setAttribute("param22","value22");
//        eventBus.fireEvent(event);
//        Assert.assertFalse(handledEvent[0]);
//        Assert.assertTrue(handledEvent[1]);
//    }
//}
