package net.itransformers.idiscover.v2.core.webCrawlerDiscoverer;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */

/*
 * IMPORTANT: Make sure that you update crawler4j.properties file and set
 * crawler.include_images to true
 */

public class MrtCrawlController {

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
}

