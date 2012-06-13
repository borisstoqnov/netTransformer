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

package net.itransformers.topologyviewer.gui;

import net.itransformers.topologyviewer.config.TopologyViewerConfType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class ViewerConfigLoader {

    public static TopologyViewerConfType loadViewerConfig(URL url) throws JAXBException, IOException {
        final Class<TopologyViewerConfType> docClass = TopologyViewerConfType.class;
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(packageName);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        Unmarshaller u = null;
        try {
            u = jc.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        JAXBElement<TopologyViewerConfType> doc = null;
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
//            inputStream = this.getClass().getResourceAsStream(fileName);
            doc = u.unmarshal( new StreamSource(inputStream), docClass );
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc.getValue();
    }

}
