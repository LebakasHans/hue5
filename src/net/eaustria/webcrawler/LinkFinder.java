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
import java.net.URL;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.List;

public class LinkFinder implements Runnable {

    private String url;
    private ILinkHandler linkHandler;
    /**
     * Used fot statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinder(String url, ILinkHandler handler) {
        this.url = url;
        this.linkHandler = handler;
    }

    @Override
    public void run() {
        getSimpleLinks(url);
    }

    private void getSimpleLinks(String url) {
        // 1. if url not already visited, visit url with linkHandler
        if(!linkHandler.visited(url)) {
            // 2. get url and Parse Website
            try {
                Parser parser = new Parser(url);
                // 3. extract all URLs and add url to list of urls which should be visited
                //    only if link is not empty and url has not been visited before
                NodeList nodesThatMatch = parser.extractAllNodesThatMatch(new TagNameFilter("a"));
                SimpleNodeIterator nodes = nodesThatMatch.elements();
                while (nodes.hasMoreNodes()) {
                    String link = ((LinkTag) nodes.nextNode()).getLink();
                    if(!link.isEmpty() && !linkHandler.visited(link)) {
                        linkHandler.queueLink(link);
                    }
                }
                linkHandler.addVisited(url);
                // 4. If size of link handler equals 500 -> print time elapsed for statistics
                if (linkHandler.size() == 500) {
                    System.out.println((System.nanoTime() - t0)/1000000000 + "s");
                }
            } catch (Exception e) {

            }
        }
    }
}
