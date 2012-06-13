/*
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

package net.itransformers.idiscover.discoverers;

import net.itransformers.idiscover.core.Discoverer;
import net.itransformers.idiscover.core.DiscovererFactory;
import net.itransformers.idiscover.core.Resource;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-18
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDiscovererFactory implements DiscovererFactory {
    private static Discoverer myDiscoverer;

    public Discoverer createDiscoverer(Resource resource) throws Exception{
        if (myDiscoverer == null) {
            myDiscoverer = new SnmpWalker(resource);
        }
        return myDiscoverer;
    }

}
