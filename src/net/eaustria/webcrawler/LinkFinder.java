/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

/**
 *
 * @author bmayr
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LinkFinder implements Runnable {

    private String url;
    private ILinkHandler linkHandler;
    private boolean distinctLinks;
    private int numberOfUrlsToVisit;

    /**
     * Used fot statistics
     */
    private long t0;

    public LinkFinder(String url, ILinkHandler handler, boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) {
        this.url = url;
        this.linkHandler = handler;
        this.distinctLinks = distinctLinks;
        this.numberOfUrlsToVisit = numberOfUrlsToVisit;
        this.t0 = startingTime;
    }

    @Override
    public void run() {
        getSimpleLinks(url);
    }

    private void getSimpleLinks(String url) {
        if (!linkHandler.isProcessStopping()) {
            if (distinctLinks && linkHandler.visited(url)) {
                return;
            }else {
                Document document;
                // 1. if url not already visited, visit url with linkHandler
                if (!linkHandler.visited(url)) {
                    // 2. get url and Parse Website
                    try {
                        document = Jsoup.connect(url).get();
                        // 3. extract all URLs and add url to list of urls which should be visited
                        //    only if link is not empty and url has not been visited before
                        Elements links = document.select("a[href]");
                        // 5. add new Action for each sublink
                        for (Element link : links) {
                            if (!link.attr("href").isEmpty() && !linkHandler.visited(link.attr("href"))) {
                                if (link.absUrl("href").startsWith("http")) {
                                    linkHandler.queueLink(link.absUrl("href"), linkHandler, distinctLinks, numberOfUrlsToVisit, t0);
                                }
                            }
                        }
                        if(distinctLinks && !linkHandler.visited(url)) {
                            linkHandler.addVisited(url);
                        }
                        if(!distinctLinks){
                            linkHandler.addVisited(url);
                        }
                        // 4. If size of link handler equals 500 -> print time elapsed for statistics
                        if (linkHandler.size() == numberOfUrlsToVisit) {
                            if (!linkHandler.isProcessStopping()) {
                                System.out.println((System.nanoTime() - t0) / 1000000 + "ms");
                            }
                            linkHandler.stopCrawling();
                            return;
                        }
                    } catch (IOException e) {
                        System.err.println("Coudn't acces website: " + e.getMessage());
                        linkHandler.addVisited(url);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

