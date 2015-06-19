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

package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import net.itransformers.topologyviewer.fulfilmentfactory.Fulfilment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFulfilmentImpl implements Fulfilment {
    public static Pattern readUntilArgsRegExp = Pattern.compile("^'(.*)',\\s*([-]?\\d+)\\s*$");
    public static Pattern readUntilRegExp = Pattern.compile("^### (?:start )?read_until\\((.*)\\)$");
    private CLIInterface cli;
    private File projectPath;

    public TestFulfilmentImpl() {

    }

    public TestFulfilmentImpl(CLIInterface adapter) {
        this.cli = adapter;
    }

    @Override
    public void fulfil(File projectPath, Map<String, String> parameters,
                       Map<String, String> fulfilmentFactoryParams, Logger logger) throws IOException {
        this.projectPath = projectPath;
        cli = new TelnetCLIInterface(parameters.get("discoveredIPv4Address"),parameters.get("username"),parameters.get("password"),parameters.get("hostname")+"#",1000, logger);
        cli.open();
        execute(fulfilmentFactoryParams.get("commands"), parameters);
        cli.close();
    }

    public void execute(String fileName, Map<String, String> params) throws IOException {
        List<String> lines = FileUtils.readLines(new File(projectPath, fileName));
        Set<String> vars = new HashSet<String>();
        Map<String, String> readUntilArgs = null;
        for (String line : lines) {
            if (line.startsWith("### vars:")) {
                vars.addAll(readVars(line));
            } else if (line.startsWith("### read_until")){
                Map<String, String> readSingleUntilArgs = parseReadUntilArgs(line, params);
                readUntil(readSingleUntilArgs);
                continue;
            } else if (line.startsWith("### start read_until")){
                readUntilArgs = parseReadUntilArgs(line, params);
                readUntil(readUntilArgs);
                continue;
            } else if (line.startsWith("### stop read_until")){
                readUntilArgs = null; // if no readUntilArgs then no read_until will be performed
            } else if (line.startsWith("### exit")){
                exit();
            } else if (line.startsWith("### //")) {
                // do nothing just a comment
            } else if (line.startsWith("###")) {
                throw new RuntimeException("Unexpected line: "+line);
            } else {
                sendCommand(line, vars, params);
            }
            if (readUntilArgs != null) {
                readUntil(readUntilArgs);
            }
        }
    }

    private void sendCommand(String line, Set<String> vars, Map<String, String> parameters) {
        for (String var : vars) {
            if (!parameters.containsKey(var)) {
                throw new RuntimeException(String.format("Can not find variable with name '%s' in parameters map %s", var, parameters.toString()));
            }
            line = line.replaceAll("\\$"+var,parameters.get(var));
        }
        cli.sendData(line);
    }

    private void exit() {
        // Do nothing
        //cli.close();
    }

    Map<String, String> parseReadUntilArgs(String line, Map<String, String> params) {
        String args = null;
        final Matcher matcher1 = readUntilRegExp.matcher(line);
        if (matcher1.find()) {
            args = matcher1.group(1);
        }
        if (args == null) {
            throw new RuntimeException("Invalid syntax of read_until command: "+line);
        }

        Map<String, String> result = new HashMap<String, String>();
        final Matcher matcher = readUntilArgsRegExp.matcher(args);
        if (matcher.find()) {
            result.put("regexp",matcher.group(1));
            result.put("timeout",matcher.group(2));
        }
        if (params != null && result.get("regexp") != null){
            String expr = result.get("regexp");
            for (String paramKey : params.keySet()) {
                expr = expr.replaceAll("\\$"+paramKey,params.get(paramKey));
            }
            result.put("regexp",expr);
        }
        return result;
    }
    private void readUntil(Map<String, String> args) throws IOException {
        final String timeoutStr = args.get("timeout");
        int timeout = Integer.parseInt(timeoutStr);
        cli.readUntil(args.get("regexp"), timeout);
    }

    Set<String> readVars(String line) {
        String varsStr = line.substring("### vars:".length()).trim();
        String[] varsArr = varsStr.split(",\\s");
        Set<String> vars = new HashSet<String>(varsArr.length);
        vars.addAll(Arrays.asList(varsArr));
        return vars;
    }

    public static void main(String[] args) throws IOException {
        TelnetCLIInterface cli1 = new TelnetCLIInterface("10.10.10.10", "user", "pass!", "hostname#", 1000, Logger.getAnonymousLogger());
        cli1.open();
        TestFulfilmentImpl ful = new TestFulfilmentImpl(cli1);
        Map<String,String> params = new HashMap<String, String>();
        params.put("username","user");
        params.put("password","pass!");
        params.put("site","hostname");
        ful.execute("fulfilment-factory/conf/txt/configureInterface.txt",params);
        cli1.close();
    }
}
