/*
 * SNMPRawDataWalkAssertionTestCases.java
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

package net.itransformers.assertions.impl;

import net.itransformers.assertions.AssertionResult;
import net.itransformers.assertions.AssertionType;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 4/22/14.
 */
public class SNMPRawDataWalkAssertionTestCases {
    @Test
    public void AssertSnmpAptiloEntry() throws FileNotFoundException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("xpath", "/root/iso/org/dod/internet/private/enterprises/aptilo/products/ale/core/coreService/coreServiceTable/coreServiceEntry[1]/index");
        params.put("expectedValue", "2.105.109");
        //This should be somethething like 'im'
        //params.put("expectedValue", "im");
        XPathAssertion assertion = new XPathAssertion("test", params);
        FileInputStream is = new FileInputStream("snmptoolkit/src/test/java/resources/aptilo.xml");
        InputSource inputSource = new InputSource(is);
        AssertionResult assertionResult = assertion.doAssert(inputSource);
        Assert.assertEquals(assertionResult.toString(),AssertionType.SUCCESS, assertionResult.getType());
    }
    @Test
    public void AssertSnmpWalkJnxUtilCounter32Entry() throws FileNotFoundException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("xpath", "//root/iso/org/dod/internet/private/enterprises/juniperMIB/jnxMibs/jnxUtilMibRoot/jnxUtil/jnxUtilData/jnxUtilCounter32Table/jnxUtilCounter32Entry[1]/index[@name='jnxUtilCounter32Name']/.");
        params.put("expectedValue", "103.101.45.48.47.48.47.48.95.99.102.109.45.97.118.101.114.97.103.101.45.111.110.101.119.97.121.45.98.107.119.100.45.100.101.108.97.121.45.118.97.114.105.97.116.105.111.110");
        //params.put("expectedValue", "103.101.45.48.47.48.47.48.95.99.102.109.45.97.118.101.114.97.103.101.45.111.110.101.119.97.121.45.98.107.119.100.45.100.101.108.97.121.45.118.97.114.105.97.116.105.111.110");
        //This should be somethething like 'ge-0/0/0_cfm-average-oneway-bkwd-delay-variation'
        XPathAssertion assertion = new XPathAssertion("test", params);
        FileInputStream is = new FileInputStream("snmptoolkit/src/test/java/resources/jnxUtilCounter32Entry.xml");
        InputSource inputSource = new InputSource(is);
        AssertionResult assertionResult = assertion.doAssert(inputSource);
        Assert.assertEquals(AssertionType.SUCCESS, assertionResult.getType());
    }
}
