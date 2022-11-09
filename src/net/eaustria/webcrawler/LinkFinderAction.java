/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 *
 * @author bmayr
 */

// Recursive Action for forkJoinFramework from Java7

public class LinkFinderAction extends RecursiveAction {

    private String url;
    private ILinkHandler cr;
    private boolean distinctLinks;
    private int numberOfUrlsToVisit;

    /**
     * Used for statistics
     */
    private long t0;

    public LinkFinderAction(String url, ILinkHandler cr, boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) {
        this.url = url;
        this.cr = cr;
        this.distinctLinks = distinctLinks;
        this.numberOfUrlsToVisit = numberOfUrlsToVisit;
        t0 = startingTime;
    }

    @Override
    public void compute() {
        if (!cr.isProcessStopping()) {
            if (distinctLinks && cr.visited(url)) {
                return;
            } else {
                Document document;
                // 2. Create new list of recursiveActions
                List<LinkFinderAction> lfa = new ArrayList<LinkFinderAction>();
                // 3. Parse url
                try {
                    document = Jsoup.connect(url).get();
                    // 4. extract all links from url
                    Elements links = document.select("a[href]");
                    // 5. add new Action for each sublink
                    for (Element link : links) {
                        //deletes all non http and https links
                        if (link.absUrl("href").startsWith("http")) {
                            lfa.add(new LinkFinderAction(link.absUrl("href"), cr, distinctLinks, numberOfUrlsToVisit, t0));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Coudn't acces website: " + e.getMessage());
                }
                if(distinctLinks && !cr.visited(url)) {
                    cr.addVisited(url);
                }
                if(!distinctLinks){
                    cr.addVisited(url);
                }
                // 6. if size of crawler exceeds 500 -> print elapsed time for statistics
                if (cr.size() == numberOfUrlsToVisit) {
                    if (!cr.isProcessStopping()) {
                        System.out.println((System.nanoTime() - t0) / 1000000 + "ms");
                    }
                    cr.stopCrawling();
                    return;
                }
                // -> Do not forget to call ìnvokeAll on the actions!
                invokeAll(lfa);
            }
        }
    }
}
