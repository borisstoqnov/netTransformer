/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.config.datamatcher.impl;/*
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

import net.itransformers.topologyviewer.config.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

public class BetweenOrEqualInterval implements DataMatcher{
    static Logger logger = Logger.getLogger(BetweenOrEqualInterval.class);
    @Override
    public boolean compareData(String data1, String data2) {
        try {
            String[] limits = data2.split(",");
            if (limits.length != 2){
                logger.error("Unable to parse input limits: "+data2);
                return false;
            }
            Integer i11 = Integer.parseInt(limits[0]);
            Integer i12 = Integer.parseInt(limits[1]);
            Integer i2 = Integer.parseInt(data1);
            logger.debug("beoq: "+i11+":"+i2+":"+i12);

            return (i2 >= i11 && i2 <= i12);
        }catch (RuntimeException rte) {
            logger.error("Unable to compare " + data1 + " and " + data2 +". Reason: "+rte.getMessage(),rte);
            return false;
        }
    }
}
