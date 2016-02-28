/*
 * AssertionExecutor.java
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

package net.itransformers.assertions;

import net.itransformers.assertions.config.AssertType;
import net.itransformers.assertions.config.AssertTypeType;
import net.itransformers.assertions.config.AssertionsType;
import net.itransformers.assertions.config.ParameterType;
import net.itransformers.utils.JaxbMarshalar;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class AssertionExecutor {
    public List<Map<File, AssertionResult>> execute(File[] inputFiles, File assertionsConfig, AssertionLevel level) throws Exception {
        AssertionsType assertionsType = JaxbMarshalar.unmarshal(AssertionsType.class,new FileInputStream(assertionsConfig));
        List<AssertType> asserts = assertionsType.getAssert();
        List<Map<File, AssertionResult>> result = new ArrayList<Map<File, AssertionResult>>();
        List<AssertTypeType> assertTypes = assertionsType.getAssertTypes().getAssertType();
        Map<String, Class> assertTypeMapping = new HashMap<String, Class>();
        for (AssertTypeType assertType : assertTypes) {
            assertTypeMapping.put(assertType.getName(),Class.forName(assertType.getClazz()));
        }

        for (AssertType anAssert : asserts) {
            Class assertClazz = assertTypeMapping.get(anAssert.getType());
            Map<File, AssertionResult> assertionResult = execute(inputFiles, anAssert, assertClazz, level);
            result.add(assertionResult);
        }
        return result;
    }

    private Map<File, AssertionResult> execute(File[] inputFiles, AssertType assertion, Class assertClazz, AssertionLevel level) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
        Map<File, AssertionResult> result = new LinkedHashMap<File, AssertionResult>();
        Constructor<Assertion> constructor = assertClazz.getConstructor(String.class, Map.class);
        List<ParameterType> paramsList = assertion.getParameter();
        Map<String, String> params = new HashMap<String, String>();
        for (ParameterType param : paramsList) {
            params.put(param.getName(),param.getValue());
        }
        Assertion inst = constructor.newInstance(assertion.getName(),params);
        for (File inputFile : inputFiles) {
            InputStream fileInputStream = new FileInputStream(inputFile);
            try {
                AssertionResult assertionResult = inst.doAssert(new InputSource(fileInputStream));
                result.put(inputFile, assertionResult);
            } catch (RuntimeException rte) {
                throw new RuntimeException("Can not assert file: "+inputFile.getAbsolutePath(), rte);
            }
        }
        return result;

    }



    public static void main(String[] args) {
        AssertionExecutor    assertResult    = new AssertionExecutor();

    }
}
