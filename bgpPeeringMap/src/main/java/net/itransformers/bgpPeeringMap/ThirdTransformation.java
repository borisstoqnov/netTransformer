/*
 * ThirdTransformation.java
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

package net.itransformers.bgpPeeringMap;

import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class ThirdTransformation {

    public static void main(String[] args) throws Exception {

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();

        XsltTransformer transformer = new XsltTransformer();
        File xsltFileName1 = new File("/Users/niau/trunk/bgpPeeringMap/conf/xslt/reorderNodes_Edges.xslt.xsl");


        byte[] rawData = readRawDataFile("/Users/niau/trunk/bgpPeeringMap/src/main/resources/bgpPeeringMap.graphml");
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, null, null);
        File outputFile1 = new File("/Users/niau/trunk/bgpPeeringMap/src/main/resources", "bgpPeeringMap2.graphxml");

        FileUtils.writeStringToFile(outputFile1, new String(outputStream1.toByteArray()));

    }
    private static byte[] readRawDataFile(String rawData) throws Exception {
        FileInputStream is = new FileInputStream(rawData);
        byte[] data = new byte[is.available()];
        is.read(data);
        return data;
    }

}