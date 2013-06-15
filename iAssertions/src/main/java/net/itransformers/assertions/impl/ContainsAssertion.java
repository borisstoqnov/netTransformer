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

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContainsAssertion implements Assertion {
    private boolean ignoreCase;
    private boolean useRegExp;
    private boolean notContains;

    public ContainsAssertion(Map<String, String> params) {
        this.ignoreCase = Boolean.parseBoolean(params.get("ignoreCase"));
        this.useRegExp = Boolean.parseBoolean(params.get("useRegExp"));
        this.notContains = Boolean.parseBoolean(params.get("notContains"));
    }

    @Override
    public AssertionResult doAssert(InputSource source) {
        return null;
    }

}
