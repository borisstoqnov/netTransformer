/*
 * XmlDiscoveryHelperFactory.java
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
        String projectPath = params.get("projectPath");

        FileInputStream is = new FileInputStream(new File(projectPath,filename));

        discoveryHelperType = JaxbMarshalar.unmarshal(DiscoveryHelperType.class,is);
        List<DeviceType> list = discoveryHelperType.getDevice();
        for (DeviceType deviceType: list) {
            deviceTypeMap.put(deviceType.getType(),deviceType);
        }
    }

    public DiscoveryHelper createDiscoveryHelper(String deviceTypeStr) {
        DeviceType deviceType = deviceTypeMap.get(deviceTypeStr);
        if (deviceType==null){
            deviceType = deviceTypeMap.get("UNKNOWN");
        }
        return new XmlDiscoveryHelper(deviceType);

    }

    public XmlDiscoveryHelperV2 createDiscoveryHelperv2(String deviceTypeStr) {
        DeviceType deviceType = deviceTypeMap.get(deviceTypeStr);
        if (deviceType==null){
            deviceType = deviceTypeMap.get("UNKNOWN");
        }
        return new XmlDiscoveryHelperV2(deviceType);

    }

}
