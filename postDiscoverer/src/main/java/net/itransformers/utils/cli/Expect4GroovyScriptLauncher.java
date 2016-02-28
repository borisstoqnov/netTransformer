/*
 * Expect4GroovyScriptLauncher.java
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

package net.itransformers.utils.cli;


import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4groovy.Expect4Groovy;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Expect4GroovyScriptLauncher {
    Binding binding;
    CLIConnection connection;
    GroovyScriptEngine gse;
     static Logger logger = Logger.getLogger(Expect4GroovyScriptLauncher.class);



    public static void main(String[] args) throws IOException, ResourceException, ScriptException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("protocol","telnet");
        params.put("username","misho");
        params.put("password","misho321");
        params.put("enablePass","misho321");
        params.put("address","87.247.249.134");
        params.put("port","22");
        params.put("command","no ip domain-lookup");

        Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();

         launcher.open(new String[]{"/postDiscoverer/conf/groovy/" + File.separator}, "cisco_login.groovy", params);
         launcher.sendCommand("cisco_sendCommand.groovy", "sh runn", "postDiscoverer/conf/groovy/cisco_config_eval.groovy");
         launcher.close("cisco_login.groovy");



      //

    }

    public Map<String,Object> sendCommand(String scriptName, String command,String evalscriptPath) throws ResourceException, ScriptException {
            binding.setProperty("command",command);
            if(evalscriptPath!=null){
                binding.setProperty("evalScript",new File(evalscriptPath));
            }else {
                binding.setProperty("evalScript",null);

            }
            Map<String, Object> result = (Map<String, Object>) gse.run(scriptName, binding);
            return result;

    }

    public Map<String,Integer>  open(String[] roots, String scriptName, Map<String, String> params) throws ResourceException, ScriptException {
        connection = createCliConnection(params);
        Map<String, Integer> result = null;
        try {
            HashMap<String, Object> convertedParams = new HashMap<String, Object>();
            convertedParams.putAll(params);
            connection.connect(convertedParams);
            binding = new Binding();
            Expect4Groovy.createBindings(connection, binding, true);
            binding.setProperty("params", params);
            gse = new GroovyScriptEngine(roots);
            result = (Map<String, Integer>) gse.run(scriptName, binding);
           if (result.get("status").equals("1")){
                return result;
            }else {
               return result;
           }

        } catch (IOException ioe){
            logger.info(ioe);
        }
        return result;
    }
    public Map<Integer,String> close(String scriptName) throws ResourceException, ScriptException {
        try {
            Map<Integer, String> result = (Map<Integer, String>) gse.run(scriptName, binding);

//            String status  = result.get("status");
           // if (status == 1)){
                connection.disconnect();
                return result;
            //}

        } catch (IOException ioe){
            logger.info(ioe);
        }
        return null;
    }

    private CLIConnection createCliConnection(Map<String, String> params) {
        CLIConnection conn;
        if ("telnet".equals(params.get("protocol"))){
            conn = new TelnetCLIConnection();
        } else if ("raw".equals(params.get("protocol"))){
            conn = new RawSocketCLIConnection();
        }  else {
            conn = new SshCLIConnection();
        }
        return conn;
    }
}
