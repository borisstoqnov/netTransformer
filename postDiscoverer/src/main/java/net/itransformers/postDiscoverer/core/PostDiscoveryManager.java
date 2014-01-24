package net.itransformers.postDiscoverer.core;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/18/14
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostDiscoveryManager {
    static Logger logger = Logger.getLogger(PostDiscoveryManager.class);
    File postDiscoveryConfing;
    File resourceManagerConfig;
    File groovyScriptsPath;
    File assertRulesConfig;

    PostDiscoveryManager(File postDiscoveryConfing, File resourceManagerConfig,File groovyScriptsPath,File assertRulesConfig){


    }

}
