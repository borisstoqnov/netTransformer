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

package net.itransformers.assertions;

import net.itransformers.assertions.config.*;
import net.itransformers.utils.CmdLineParser;
import net.itransformers.utils.JaxbMarshalar;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssertionExecutor {
    public AssertionResult[] execute(File[] inputFiles, File assertionsConfig, AssertionLevel level) throws Exception {
        AssertionsType assertionsType = JaxbMarshalar.unmarshal(AssertionsType.class,new FileInputStream(assertionsConfig));
        List<AssertType> asserts = assertionsType.getAssert();
        List<AssertionResult> result = new ArrayList<AssertionResult>();
        List<AssertTypeType> assertTypes = assertionsType.getAssertTypes().getAssertType();
        Map<String, Class> assertTypeMapping = new HashMap<String, Class>();
        for (AssertTypeType assertType : assertTypes) {
            assertTypeMapping.put(assertType.getName(),Class.forName(assertType.getClazz()));
        }

        for (AssertType anAssert : asserts) {
            Class assertClazz = assertTypeMapping.get(anAssert.getType());
            List<AssertionResult> assertionResult = execute(inputFiles, anAssert, assertClazz, level);
            result.addAll(assertionResult);
        }
        return result.toArray(new AssertionResult[result.size()]);
    }

    private List<AssertionResult> execute(File[] inputFiles, AssertType assertion, Class assertClazz, AssertionLevel level) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
        List<AssertionResult> result = new ArrayList<AssertionResult>();
        Constructor<Assertion> constructor = assertClazz.getConstructor(Map.class);
        List<ParameterType> paramsList = assertion.getParameter();
        Map<String, String> params = new HashMap<String, String>();
        for (ParameterType param : paramsList) {
            params.put(param.getName(),param.getValue());
        }
        Assertion inst = constructor.newInstance(params);
        for (File inputFile : inputFiles) {
            InputStream fileInputStream = new FileInputStream(inputFile);
            AssertionResult assertionResult = inst.doAssert(new InputSource(fileInputStream));
            result.add(assertionResult);
        }
        return result;

    }
}
