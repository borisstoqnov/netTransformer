

/*
 * javaMrtCrawler.java
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

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.util.regex.Pattern;

public class javaMrtCrawler extends WebCrawler {

  private static final Pattern filters = Pattern.compile(".*bview.*(\\.(bin|tar.gz|bz|bz2|zip|rar|gz))$");

 // private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");

  private static File storageFolder;
  private static String[] crawlDomains;

  public static void configure(String[] domain, String storageFolderName) {
    javaMrtCrawler.crawlDomains = domain;

    storageFolder = new File(storageFolderName);
    if (!storageFolder.exists()) {
      storageFolder.mkdirs();
    }
  }

  public boolean shouldVisit(Page page, WebURL url) {
    String href = url.getURL().toLowerCase();

    if (filters.matcher(href).matches()) {
      return true;
    }

    for (String domain : crawlDomains) {
      if (href.startsWith(domain)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();
      System.out.println(url);
    // We are only interested in processing images
    if (!(page.getParseData() instanceof BinaryParseData)) {
      return;
    }

    if (!filters.matcher(url).matches()) {
      return;
    }

    // Not interested in very small images
//    if (page.getContentData().length < 10 * 1024) {
//      return;
//    }

    // get a unique name for storing this image
    String extension = url.substring(url.lastIndexOf("."));
    String hashedName = Cryptography.MD5(url) + extension;

    // store image
    //IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + hashedName);

    System.out.println("Stored: " + url+" with name "+hashedName);
  }
}
