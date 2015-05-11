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

package net.itransformers.idiscover.discoveryhelpers.xml;

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryHelperFactory;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DeviceType;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DiscoveryHelperType;
import net.itransformers.idiscover.util.JaxbMarshalar;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlDiscoveryHelperFactory implements DiscoveryHelperFactory {
    private DiscoveryHelperType discoveryHelperType;
    private Map<String, DeviceType> deviceTypeMap = new HashMap<String, DeviceType>();


    public XmlDiscoveryHelperFactory(Map<String, String> params) throws JAXBException, FileNotFoundException {
        String filename = params.get("fileName");
        FileInputStream is = new FileInputStream(new File(System.getProperty("base.dir"),filename));
        discoveryHelperType = JaxbMarshalar.unmarshal(DiscoveryHelperType.class,is);
        List<DeviceType> list = discoveryHelperType.getDevice();
        for (DeviceType deviceType: list) {
            deviceTypeMap.put(deviceType.getType(),deviceType);
        }
    }
//    public XmlDiscoveryHelperFactory(InputStream is) throws JAXBException {
//        discoveryHelperType = JaxbMarshalar.unmarshal(DiscoveryHelperType.class,is);
//        List<DeviceType> list = discoveryHelperType.getDevice();
//        for (DeviceType deviceType: list) {
//            deviceTypeMap.put(deviceType.getType(),deviceType);
//        }
//    }

    public DiscoveryHelper createDiscoveryHelper(String deviceTypeStr) {
        DeviceType deviceType = deviceTypeMap.get(deviceTypeStr);
        if (deviceType==null){
            deviceType = deviceTypeMap.get("UNKNOWN");
        }
        return new XmlDiscoveryHelper(deviceType, discoveryHelperType.getStopCriteria());

    }

}
