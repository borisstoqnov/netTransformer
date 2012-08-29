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

import groovy.lang.GroovyClassLoader;
import net.itransformers.topologyviewer.fulfilmentfactory.Fulfilment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class GroovyCLIFulfilmentImpl implements Fulfilment {
    private CLIInterface cli;

    public GroovyCLIFulfilmentImpl() {

    }

    public GroovyCLIFulfilmentImpl(CLIInterface adapter) {
        this.cli = adapter;
    }

    @Override
    public void fulfil(Map<String, String> parameters,
                       Map<String, String> fulfilmentFactoryParams, Logger logger) throws IOException {
        cli = new TelnetCLIInterface(parameters.get("ManagementIPAddress"),parameters.get("username"),parameters.get("password"),parameters.get("hostname")+"#",1000, logger);
//        cli.open();
        try {
            execute(fulfilmentFactoryParams.get("commands"), parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
  //      cli.close();
    }

    public void execute(String fileName, Map<String, String> params) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class scriptClass = new GroovyClassLoader().parseClass( new File( fileName ) ) ;
        Object scriptInstance = scriptClass.newInstance() ;
        scriptClass.getDeclaredMethod( "execute", new Class[] {CLIInterface.class, Map.class } ).invoke(scriptInstance, cli, params) ;
    }


    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TelnetCLIInterface cli1 = new TelnetCLIInterface("10.10.10.10", "user", "pass!", "hostname#", 1000, Logger.getAnonymousLogger());
        //cli1.open();
        GroovyCLIFulfilmentImpl ful = new GroovyCLIFulfilmentImpl(cli1);
        Map<String,String> params = new HashMap<String, String>();
        params.put("username","user");
        params.put("password","pass!");
        params.put("site","hostname");
        ful.execute("iTopologyManager/fulfilmentFactory/src/main/resources/groovy/test.groovy",params);
        cli1.close();
    }
}
