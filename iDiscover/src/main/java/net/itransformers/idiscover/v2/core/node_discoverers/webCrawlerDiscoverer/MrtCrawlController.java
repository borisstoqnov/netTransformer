/*
 * MrtCrawlController.java
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

package net.itransformers.idiscover.v2.core.node_discoverers.webCrawlerDiscoverer;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.ProjectConstants;
import org.apache.commons.io.output.NullOutputStream;
import org.javamrt.dumper.CmdLineParser;
import org.javamrt.dumper.Route2GraphmlDumper;
import org.javamrt.dumper.structures.ASContainer;

import java.io.*;
import java.util.Map;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */

/*
 * IMPORTANT: Make sure that you update crawler4j.properties file and set
 * crawler.include_images to true
 */

public class MrtCrawlController implements NodeDiscoverer {

  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.out.println("Needed parameters: ");
      System.out.println("\t rootFolder (it will contain intermediate crawl data)");
      System.out.println("\t numberOfCralwers (number of concurrent threads)");
      System.out.println("\t storageFolder (a folder for storing downloaded images)");
      return;
    }

    String rootFolder = args[0];
    int numberOfCrawlers = Integer.parseInt(args[1]);
    String storageFolder = args[2];

    CrawlConfig config = new CrawlConfig();

    config.setCrawlStorageFolder(rootFolder);

    /*
     * Since images are binary content, we need to set this parameter to
     * true to make sure they are included in the crawl.
     */
    config.setIncludeBinaryContentInCrawling(true);

    String[] crawlDomains = new String[] { "http://data.ris.ripe.net/rrc00" };

    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    edu.uci.ics.crawler4j.crawler.CrawlController controller = new edu.uci.ics.crawler4j.crawler.CrawlController(config, pageFetcher, robotstxtServer);
    for (String domain : crawlDomains) {
      controller.addSeed(domain);
    }

    javaMrtCrawler.configure(crawlDomains, storageFolder);

    controller.start(javaMrtCrawler.class, numberOfCrawlers);
  }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        NodeDiscoveryResult result = new NodeDiscoveryResult(null, null);
        String[] args = new String[4];
        args[0] = "-f";


      //  args[1] = pathToFile;


        args[2] = "-o";
        String outputFile = ProjectConstants.networkGraphmlFileName;
        args[3] = outputFile;

        Map<String, String> params = CmdLineParser.parseCmdLine(args);



        StringWriter writer = new StringWriter();

        String file = null;
        OutputStream logOutputStream = new NullOutputStream();

        if (params.containsKey("-f")) {
            file = params.get("-f");
        } else {
         //  logger.info("no file passed");
            System.exit(1);
        }

        ASContainer ases = new ASContainer();
        System.out.println("Start reading MRT file");
        File tmpEdgeFile = null;
        try {
            tmpEdgeFile = File.createTempFile("test" + "_", ".txt");
            System.out.println("Creating edge tmp file: "+tmpEdgeFile.getAbsolutePath());
            Writer edgeWriter =  new PrintWriter(tmpEdgeFile);

            Route2GraphmlDumper.dumpToXmlString(new String[]{file}, new PrintWriter(logOutputStream), edgeWriter, ases);
            edgeWriter.close();
            Route2GraphmlDumper.dumpGraphml(ases, writer, tmpEdgeFile);

        }catch (IOException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        } finally {
            tmpEdgeFile.delete();


        }


//        logger.debug ("Node id: " + nodeId+ "\n");
//        logger.debug ("Date: " + date + "\n");
      //  result.setNodeId(nodeId);
      //  result.setDiscoveredData("version",version);
        result.setDiscoveredData("graphml",writer.toString().getBytes());
        return result;
    }
}

