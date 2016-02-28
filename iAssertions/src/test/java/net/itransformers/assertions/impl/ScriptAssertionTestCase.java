/*
 * ScriptAssertionTestCase.java
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
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/16/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptAssertionTestCase {
    @Test
    public void assertDoAssert() throws FileNotFoundException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("script",
                "def records = document.documentElement\n" +
                "use (groovy.xml.dom.DOMCategory) {\n" +
                "records.BOOKS.BOOK.findAll{ it.'@isbn' == 'DFGH09093232'}; \n" +
                "return true\n" +
                "}\n");
        ScriptAssertion assertion = new ScriptAssertion("test",params);
        FileInputStream is = new FileInputStream("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml");
        InputSource inputSource = new InputSource(is);
        AssertionResult assertionResult = assertion.doAssert(inputSource);
        Assert.assertEquals(AssertionType.SUCCESS, assertionResult.getType());
    }

}
