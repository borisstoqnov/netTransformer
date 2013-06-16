package net.itransformers.assertions;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;


public class AssertionExecutorTestCase {
    @Test
    public void testExecute() throws Exception {
        AssertionExecutor executor = new AssertionExecutor();
        File assertionsConfig = new File("iAssertions/src/test/java/net/itransformers/assertions/test-assertions-config.xml");
        File[] inputFiles = new File[]{new File("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml")};
        File folder = new File(".");

        List<Map<File, AssertionResult>> result = executor.execute(inputFiles, assertionsConfig, AssertionLevel.CRITICAL);
        for (Map<File, AssertionResult> fileAssertionResultMap : result) {
            for (File file : fileAssertionResultMap.keySet()) {
                AssertionResult assertionResult = fileAssertionResultMap.get(file);
                Assert.assertEquals(assertionResult.getType(),AssertionType.SUCCESS);
            }
        }
    }
    @Test
    public void testExecuteIPv4_IPv6_version3() throws Exception {
        String versionDir = "iAssertions/src/main/resources/network/version3";
        AssertionExecutor executor = new AssertionExecutor();
        File assertionsConfig = new File("iAssertions/src/test/java/net/itransformers/assertions/IPv4_IPv6.xml");
        final File folder = new File(versionDir);
        File[] inputFiles =folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml") &&pathname.getName().contains("device-data") ;
            }
        });

       // File[] inputFiles = new File[]{new File("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml")};
        List<Map<File, AssertionResult>> result = executor.execute(inputFiles, assertionsConfig, AssertionLevel.CRITICAL);
        for (Map<File, AssertionResult> fileAssertionResultMap : result) {
            for (Map.Entry<File, AssertionResult> fileAssertionResultEntry : fileAssertionResultMap.entrySet()) {
                System.out.println("Assert Result for '" + fileAssertionResultEntry.getKey().getName()+"': "+fileAssertionResultEntry.getValue());
            }
        }

    }
    @Test
    public void testExecuteIPv4_IPv6_version4() throws Exception {
        String versionDir = "iAssertions/src/main/resources/network/version4";
        AssertionExecutor executor = new AssertionExecutor();
        File assertionsConfig = new File("iAssertions/src/test/java/net/itransformers/assertions/IPv4_IPv6.xml");
        final File folder = new File(versionDir);
        File[] inputFiles =folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml") &&pathname.getName().contains("device-data") ;
            }
        });

        // File[] inputFiles = new File[]{new File("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml")};
        List<Map<File, AssertionResult>> result = executor.execute(inputFiles, assertionsConfig, AssertionLevel.CRITICAL);
        for (Map<File, AssertionResult> fileAssertionResultMap : result) {
            for (Map.Entry<File, AssertionResult> fileAssertionResultEntry : fileAssertionResultMap.entrySet()) {
                System.out.println("Assert Result for '" + fileAssertionResultEntry.getKey().getName()+"': "+fileAssertionResultEntry.getValue());
            }
        }

    }
}
