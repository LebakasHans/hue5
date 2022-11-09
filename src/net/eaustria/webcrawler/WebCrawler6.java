/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 *
 * @author bmayr
 */
public class WebCrawler6 implements ILinkHandler {

//    private final Collection<String> visitedLinks = Collections.synchronizedSet(new HashSet<String>());
    private final Collection<String> visitedLinks = Collections.synchronizedList(new ArrayList<String>());
    private String url;
    private ExecutorService execService;
    private static ExecutorService staticExecService;

    public WebCrawler6(String startingURL, int maxThreads) {
        this.url = startingURL;
        execService = Executors.newFixedThreadPool(maxThreads);
        staticExecService = execService;
    }

    @Override
    public void queueLink(String link, ILinkHandler linkHandler, boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) throws Exception {
        startNewThread(link, distinctLinks, numberOfUrlsToVisit, startingTime);
    }

    @Override
    public int size() {
        return visitedLinks.size();
    }

    @Override
    public void addVisited(String s) {
        visitedLinks.add(s);
    }

    @Override
    public boolean visited(String s) {
        return visitedLinks.contains(s);
    }

    private void startNewThread(String link, boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) throws Exception {
        if(!isProcessStopping()) {
            execService.execute(new LinkFinder(link, this, distinctLinks, numberOfUrlsToVisit, startingTime));
        }
    }

    private void startCrawling(boolean distinctLinks, int numberOfUrlsToVisit, long startingTime) throws Exception {
        startNewThread(this.url, distinctLinks, numberOfUrlsToVisit, startingTime);
    }

    @Override
    public void stopCrawling() {
        execService.shutdownNow();
        visitedLinks.clear();
    }

    @Override
    public boolean isProcessStopping() {
        return execService.isShutdown();
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
                new WebCrawler6("http://www.orf.at", 10).startCrawling(true, 1500, System.nanoTime());
            }
            else if (input == 2) {
                new WebCrawler6("http://www.orf.at", 10).startCrawling(false, 3000, System.nanoTime());
            }
            staticExecService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }while (input != 3);
    }
}