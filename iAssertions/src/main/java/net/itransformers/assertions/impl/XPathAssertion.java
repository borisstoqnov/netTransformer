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

package net.itransformers.assertions.impl;

import net.itransformers.assertions.Assertion;
import net.itransformers.assertions.AssertionLevel;
import net.itransformers.assertions.AssertionResult;
import org.xml.sax.InputSource;

import java.lang.IllegalArgumentException;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class XPathAssertion implements Assertion {
    private final String xPath;
    private final Map<String, String> declareNamespaces = new HashMap<String, String>();
    private final String expectedValue;
    private final Map<String, String> options = new HashMap<String, String>();

    public XPathAssertion(Map<String, String> params){
        this.xPath = params.get("xPath");
        String paramNamespaces = params.get("declareNamespaces");
        String[] nameSpacesArr = paramNamespaces.split(",");
        for (String nameSpace : nameSpacesArr) {
            String[] entries = nameSpace.split(",");
            if (entries.length != 2) throw new IllegalArgumentException("Invalid parameter declareNamespaces:"+declareNamespaces);
            this.declareNamespaces.put(entries[0], entries[1]);
        }
        this.expectedValue = params.get("expectedValue");
    }

    @Override
    public AssertionResult doAssert(InputSource source) {
        return null;
    }

}
