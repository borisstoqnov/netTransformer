/*
 * iDiscovery is an open source tool able to discover IP networks
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

package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import net.itransformers.topologyviewer.fulfilmentfactory.Fulfilment;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class XslFulfilmentImpl implements Fulfilment {

   XsltTransform tranformer;

    public XslFulfilmentImpl() {

    }
    public void fulfil(Map<String, String> parameters,
                       Map<String, String> fulfilmentFactoryParams, Logger logger) throws IOException{

        this.tranformer = new XsltTransform(fulfilmentFactoryParams.get("xslFileName"));
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" ?>\n");
                sb.append("<root>\n");
                sb.append("<parameters>\n");
                 if (parameters != null) {
                    for (String param: parameters.keySet()){
                        sb.append("<name>" + param + "</name>\n");
                        sb.append("<value>" + parameters.get(param) + "</value>\n");
                    }
                }
                sb.append("</parameters>\n");
                sb.append("<fulfilmentFactoryParams>\n");
                 if (fulfilmentFactoryParams != null) {
                    for (String param: fulfilmentFactoryParams.keySet()){
                        sb.append("<name>" + param + "</name>\n");
                        sb.append("<value>" + fulfilmentFactoryParams.get(param) + "</value>\n");
                    }
                }
                sb.append("</fulfilmentFactoryParams>\n");
                sb.append("</root>");
        try {
         logger.info("Input XML \n"+ sb.toString());
         tranformer.myTransformer(sb.toString());

        } catch (TransformerException e) {
            logger.info("xsl Fulfillment error message:" + e.getMessage());
        }


    }


    public static void main(String[] args) throws IOException, JAXBException {
//        Map<String,Object> context = createContext();
//        XslFulfilmentImpl  XslFulfill  = new XslFulfilmentImpl("fulfillment-factory/conf/xslt/setDeviceLocation.xslt");
//        ParameterFactoryBuilder builder = new ParameterFactoryBuilder("parameter-factory/conf/xml/param-factory.xml");
//        ResourceManager resourceManager = new ResourceManager("resource-manager/conf/xml/resource.xml");
//
//        Map<String,String> params = new HashMap<String, String>();
//        params.put("DeviceType","CISCO");
//
//        ResourceType resource = resourceManager.findResource(params);
//        FulfilmentAdapterFactory factory = new FulfilmentAdapterFactory("fulfilment-factory/conf/xml/fulfilment-factory.xml",
//                builder,resource);
//        Logger logger = Logger.getAnonymousLogger();
//        FulfilmentAdapter fulfilment = factory.createFulfilmentAdapter("testXSL", context,logger);
//        XslFulfill.fulfil();

    }

}
