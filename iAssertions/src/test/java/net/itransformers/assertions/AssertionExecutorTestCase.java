/*
 * AssertionExecutorTestCase.java
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

import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;


public class AssertionExecutorTestCase {
//    @Test
//    public void testExecute() throws Exception {
//        AssertionExecutor executor = new AssertionExecutor();
//        File assertionsConfig = new File("iAssertions/src/test/java/net/itransformers/assertions/test-assertions-config.xml");
//     //   File[] inputFiles = new File[]{new File("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml")};
//           File[] inputFiles = new File[]{new File("iAssertions/src/test/java/net/itransformers/assertions/impl/xpath-test.xml")};
//
//        File folder = new File(".");
//
//        List<Map<File, AssertionResult>> result = executor.execute(inputFiles, assertionsConfig, AssertionLevel.CRITICAL);
//        for (Map<File, AssertionResult> fileAssertionResultMap : result) {
//            for (File file : fileAssertionResultMap.keySet()) {
//                AssertionResult assertionResult = fileAssertionResultMap.get(file);
//                Assert.assertEquals(assertionResult.getType(),AssertionType.SUCCESS);
//            }
//        }
//    }
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
