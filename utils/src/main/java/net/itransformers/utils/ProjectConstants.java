/*
 * ProjectConstants.java
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

package net.itransformers.utils;

/**
 * Created by niau on 3/3/16.
 */
public class ProjectConstants {

    public static final String snmpProjectName = "SNMP Network Discovery ";
    public static final String snmpBgpDiscovererName = "Snmp BGP Peering Map";
    public static final String mrtBgpDiscovererName = "Mrt BGP Peering Map";
    public static final String freeGraphDiscovererName = "Free Graph";

    public static final String snmpProjectType = "snmpDiscoverer";
    public static final String snmpBgpDiscovererProjectType = "bgpMapSnmpDiscoverer";
    public static final String mrtBgpDiscovererProjectType = "mrtMapSnmpDiscoverer";
    public static final String freeGraphProjectType = "freeGraph";


    public static final String networkDirName = "network";
    public static final String labelDirName = "version";
    public static final String undirectedGraphmlDirName = "graphml-undirected";
    public static final String directedGraphmlDirName = "graphml-directed";


    public static String getProjectName(String projectType) {
        if (projectType.equals(ProjectConstants.snmpProjectType))

            return ProjectConstants.snmpProjectName;
        else if (projectType.equals(ProjectConstants.snmpBgpDiscovererProjectType))
            return ProjectConstants.snmpBgpDiscovererName;
        else if (projectType.equals(ProjectConstants.mrtBgpDiscovererProjectType))
            return ProjectConstants.mrtBgpDiscovererName;
        else if (projectType.equals(ProjectConstants.freeGraphProjectType))
            return ProjectConstants.freeGraphDiscovererName;
        else
            return "unknown";
    }


    public static String getProjectType(String projectName) {
        if (projectName.equals(ProjectConstants.snmpProjectName))

            return ProjectConstants.snmpProjectType;
        else if (projectName.equals(ProjectConstants.snmpBgpDiscovererName))
            return ProjectConstants.snmpBgpDiscovererProjectType;
        else if (projectName.equals(ProjectConstants.mrtBgpDiscovererName))
            return ProjectConstants.mrtBgpDiscovererProjectType;
        else if (projectName.equals(ProjectConstants.freeGraphDiscovererName))
            return ProjectConstants.freeGraphProjectType;
        else
            return "unknown";
    }


}
