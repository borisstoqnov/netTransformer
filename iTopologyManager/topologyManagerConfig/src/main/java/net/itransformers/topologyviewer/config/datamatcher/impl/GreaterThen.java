/*
 * GreaterThen.java
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

package net.itransformers.topologyviewer.config.datamatcher.impl;

import net.itransformers.topologyviewer.config.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

public class GreaterThen implements DataMatcher{
    static Logger logger = Logger.getLogger(GreaterThen.class);
    @Override
    public boolean compareData(String data1, String data2) {
        try {
            Integer i1 = Integer.parseInt(data1);
            Integer i2 = Integer.parseInt(data2);
            logger.debug("GT: "+i1+">"+i2);
            return i1 > i2;
        }catch (RuntimeException rte) {
            logger.error("Unable to compare " + data1 + " and " + data2 +". Reason: "+rte.getMessage(),rte);
            return false;
        }
    }
}
