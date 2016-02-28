/*
 * StringContains.java
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

public class StringContains implements DataMatcher {
    static Logger logger = Logger.getLogger(StringContains.class);

    @Override
    public boolean compareData(String data1, String data2) {
        if (data1 != null) {
            logger.debug("SC: "+data1+"contains"+data2);

            return data1.contains(data2);
        } else {
            return false;
        }
    }
}
