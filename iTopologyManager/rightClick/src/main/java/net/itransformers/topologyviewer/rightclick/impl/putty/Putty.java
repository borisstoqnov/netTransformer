/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.rightclick.impl.putty;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class Putty {
    private Map<String, String> params;

    public Putty(Map<String, String> params) {
        this.params = params;
    }

    public void openSession(Map<String, String> connParams){
        this.openSession(connParams.get("protocol"), connParams.get("username"), connParams.get("password"), connParams.get("ManagementIPAddress"),connParams.get("session"));
    }

    public void openSession(String protocol, String userName, String password, String host,String session){
        Runtime r = Runtime.getRuntime();
        Process p = null;
        String s;
        String pyttyPath = System.getProperty("user.dir")+ File.separator+params.get("puttyRelativePath");
        System.out.println(pyttyPath);
        if ("ssh".equals(protocol)) {
            if (session.isEmpty()|| session==null){
                s = String.format(pyttyPath+" "+params.get("ssh_no_saved_session"),userName,password,host);
            }   else {
                s = String.format(pyttyPath+" "+params.get("ssh_saved_session"),userName,password,session,host);
            }
        } else if ("telnet".equals(protocol)){
            if (session.isEmpty()|| session==null){
                s = String.format(pyttyPath+" "+params.get("telnet_no_saved_session"),userName,host);
            }   else{
                 s = String.format(pyttyPath+" "+params.get("telnet_saved_session"),userName,session,host);
            }
        } else {
            throw new IllegalArgumentException("Invalid protocol is specified: "+protocol);
        }
        try
        {
            p = r.exec(s);
//            p.waitFor();
        } catch (Exception e) {
            System.out.println("Exception error :"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String,String> rightClickParams = new HashMap<String, String>();
        System.getProperty("user.dir");
        rightClickParams.put("ssh_no_saved_session","lib/putty/putty.exe -ssh -l %s -pw %s %s");
//        rightClickParams.put("ssh_no_saved_session","lib/putty/putty.exe -ssh -l %s -pw %s %s");
        Putty putty = new Putty(rightClickParams);
        putty.openSession("ssh","user","userpass","localhost",null);
    }
}
