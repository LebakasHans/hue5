package net.eaustria.webcrawler;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bmayr
 */
public class WebCrawler7 implements ILinkHandler {

//    private final Collection<String> visitedLinks = Collections.synchronizedSet(new HashSet<String>());
    private final Collection<String> visitedLinks = Collections.synchronizedList(new ArrayList<String>());
    private String url;
    private ForkJoinPool mainPool;

    public WebCrawler7(String startingURL, int maxThreads) {
        this.url = startingURL;
        mainPool = new ForkJoinPool(maxThreads);
    }

    private void startCrawling(boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) {
        try {
            mainPool.invoke(new LinkFinderAction(url, this, distinctLinks, numberOfUrlsToVisit, startingTime));
        }catch (CancellationException ex){
            System.err.println();
        }
    }

    @Override
    public void stopCrawling() {
        mainPool.shutdownNow();
        visitedLinks.clear();
    }

    @Override
    public boolean isProcessStopping() {
        return mainPool.isTerminating();
    }

    @Override
    public int size() {
        return visitedLinks.size();
    }

    @Override
    public synchronized void addVisited(String s) {
        var test = visitedLinks.add(s);
    }

    @Override
    public boolean visited(String s) {
        return visitedLinks.contains(s);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int input;
        do {
            System.out.println("What do you want to test?(in milliseconds)");
            System.out.println("1... Search coverage");
            System.out.println("2... Processing power");
            System.out.println("3... Exit");
            input = sc.nextInt();
            if (input == 1) {
                new WebCrawler7("http://www.orf.at", 10).startCrawling(true, 1500, System.nanoTime());
            }
            else if (input == 2) {
                new WebCrawler7("http://www.orf.at", 10).startCrawling(false, 3000, System.nanoTime());
            }
        }while (input != 3);
    }

    // Just override - we do not need this methode when using forkJoinPool
    @Override
    public void queueLink(String link, ILinkHandler linkHandler, boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

