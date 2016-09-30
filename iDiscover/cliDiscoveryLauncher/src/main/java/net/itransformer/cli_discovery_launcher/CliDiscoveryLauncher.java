package net.itransformer.cli_discovery_launcher;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscovererFactory;
import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;
import net.itransformers.idiscover.api.models.network.Node;
import net.itransformers.utils.CmdLineParser;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public class CliDiscoveryLauncher {
    static Logger logger = Logger.getLogger(CliDiscoveryLauncher.class);

    public static void main(String[] args) throws MalformedURLException {
        logger.debug("iDiscover v2. gearing up");
        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");

        Map<String, String> params = CmdLineParser.parseCmdLine(args);
        String projectPath = params.get("-p");

        if (projectPath == null) {
            File cwd = new File(".");
            System.out.println("Project path is not specified. Will use current dir: " + cwd.getAbsolutePath());
            projectPath = cwd.getAbsolutePath();
        }

        File workingDir = new File(projectPath);
        if (!workingDir.exists()) {
            System.out.println("Invalid project path!");
            return;
        }

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:cliDiscoveryLauncher/cliDiscoveryLauncher.xml");
        ctx.refresh();

        NetworkDiscovererFactory discovererFactory = ctx.getBean("networkDiscoveryFactory", NetworkDiscovererFactory.class);
        VersionManagerFactory versionManagerFactory = ctx.getBean("versionManagerFactory", VersionManagerFactory.class);
        Map<String, String> props = new HashMap<>();
        props.put("projectPath", projectPath);
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);
        String version = versionManager.createVersion();
        props.put("version", version);
        NetworkDiscoverer networkDiscoverer = discovererFactory.createNetworkDiscoverer("parallel", props);


        networkDiscoverer.addNetworkDiscoveryListeners(result -> {
            Map<String, Node> nodes = result.getNodes();
            for (String node : nodes.keySet()) {
                System.out.println("Discovered node: " + node);
            }
        });
        ConnectionDetailsManagerFactory factory = ctx.getBean("connectionManagerFactory",
                ConnectionDetailsManagerFactory .class);

        ConnectionDetailsManager connectionDetailsManager = factory.createConnectionDetailsManager("csv", props);
        Map<String, ConnectionDetails> connectionDetails = connectionDetailsManager.getConnectionDetails();
        networkDiscoverer.startDiscovery(new HashSet<>(connectionDetails.values()));
    }
}
