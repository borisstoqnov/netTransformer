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
