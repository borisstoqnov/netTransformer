/*
 * ContainsAssertion.java
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

import net.itransformers.assertions.Assertion;
import net.itransformers.assertions.AssertionResult;
import net.itransformers.assertions.AssertionType;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.Map;


public class ContainsAssertion implements Assertion {
    private final String expectedValue;
    private final String assertionName;
    private boolean ignoreCase;
    private boolean useRegExp;
    private boolean notContains;

    public ContainsAssertion(String assertionName, Map<String, String> params) {
        this.assertionName = assertionName;
        this.ignoreCase = Boolean.parseBoolean(params.get("ignoreCase"));
        this.useRegExp = Boolean.parseBoolean(params.get("useRegExp"));
        this.notContains = Boolean.parseBoolean(params.get("notContains"));
        this.expectedValue = params.get("expectedValue");
    }

    @Override
    public AssertionResult doAssert(InputSource source) {
        InputStream is = source.getByteStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0 ){
                bos.write(buff,0,len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String str = new String(bos.toByteArray());
        boolean contains = contains(str);
        if (notContains){
            if (contains) {
                return new AssertionResult(assertionName, AssertionType.FAILED);
            } else {
                return new AssertionResult(assertionName, AssertionType.SUCCESS);
            }
        } else {
            if (contains){
                return new AssertionResult(assertionName, AssertionType.SUCCESS);
            } else {
                return new AssertionResult(assertionName, AssertionType.FAILED);
            }
        }
    }

    private boolean contains(String str) {
        if (useRegExp) {
            return str.matches(expectedValue);
        } else if (ignoreCase){
            return str.toLowerCase().contains(expectedValue.toLowerCase());
        } else {
            return str.contains(expectedValue);
        }
    }

}
