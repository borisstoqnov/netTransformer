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

package net.itransformers.idiscover.v2.core;/*
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

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SimpleNodeDiscoveryFilter implements NodeDiscoverFilter {
    static Logger logger = Logger.getLogger(SimpleNodeDiscoveryFilter.class);

    List<String> matches;
    List<String> notMatches;

    @Override
    public boolean match(ConnectionDetails details) {
        String host = details.getParam("host");
        for (String notMatch : notMatches){
            String propertyName = notMatch.substring(0,notMatch.indexOf("="));
            String propVal = host;
            String notMatchVal = notMatch.substring(notMatch.indexOf("=")+1);
            logger.debug("PropName="+propertyName+",propVal="+propVal+",notMatch.value="+notMatchVal);
            if (propVal != null && propVal.matches(notMatchVal)) {
                return false;
            }
        }
        for (String match : matches){
            String propertyName = match.substring(0,match.indexOf("="));
            String propVal = host;
            String matchVal = match.substring(match.indexOf("=")+1);
            logger.debug("PropName="+propertyName+",propVal="+propVal+",Match.value="+matchVal);
            if (propVal != null && propVal.matches(matchVal)) {
                return true;
            }
        }
        return false;

    }

    public void setMatches(List<String> matches) {
        for (String match : matches) {
            if (!match.contains("=")) throw new IllegalArgumentException("match: "+match+", does not contain '=");
        }
        this.matches = matches;
    }

    public void setNotMatches(List<String> notMatches) {
        for (String notMatch : notMatches) {
            if (!notMatch.contains("=")) throw new IllegalArgumentException("notMatch: "+notMatch+", does not contain '=");
        }
        this.notMatches = notMatches;
    }
}
