package facades;

import dto.TagDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//import jdk.jshell.spi.ExecutionControl;


public class WebScraper {

    public static List<TagCounter> runSequental() {
        List<TagCounter> urls = new ArrayList();
        urls.add(new TagCounter("https://www.fck.dk"));
        urls.add(new TagCounter("https://www.google.com"));
        urls.add(new TagCounter("https://politiken.dk"));
        urls.add(new TagCounter("https://cphbusiness.dk"));
        for (TagCounter tc : urls) {
            tc.doWork();
        }
        return urls;
    }

    public static List<TagDTO> runParrallel() throws NotImplementedException, InterruptedException, ExecutionException {
        String[] hostList = {"https://www.fck.dk", "https://www.google.com", "https://politiken.dk", "https://cphbusiness.dk"};
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<TagDTO>> futures = new ArrayList<>();
        List<TagCounter> tagList = new ArrayList();
        List<TagDTO> dtoList = new ArrayList();

        for (String url : hostList) {
            Callable<TagDTO> urlTask = new GetTagDTO(url);
            Future future = executor.submit(urlTask);
            futures.add(future);
        }

        for (Future<TagDTO> f : futures) {
            dtoList.add(f.get());
        }
//        for (int i = 0; i <= 4; i++) {
//            Callable<String> task = new GetTagDTO(new TagCounter())
//        }

        return dtoList;
    }

    public static void main(String[] args) throws Exception {
        long timeSequental;
        long start = System.nanoTime();

        List<TagCounter> fetchedData = new WebScraper().runSequental();
        long end = System.nanoTime();
        timeSequental = end - start;
        System.out.println("Time Sequential: " + ((timeSequental) / 1_000_000) + " ms.");

        for (TagCounter tc : fetchedData) {
            System.out.println("Title: " + tc.getTitle());
            System.out.println("Div's: " + tc.getDivCount());
            System.out.println("Body's: " + tc.getBodyCount());
            System.out.println("----------------------------------");
        }

        start = System.nanoTime();
        //TODO Add your parrallel calculation here  
        List<TagDTO> parallelCounters = runParrallel();
        long timeParallel = System.nanoTime() - start;

        for (TagDTO tc : parallelCounters) {
            System.out.println("Title: " + tc.url);
            System.out.println("Div's: " + tc.divCount);
            System.out.println("Body's: " + tc.bodyCount);
            System.out.println("----------------------------------");
        }
        System.out.println("Time Parallel: " + ((timeParallel) / 1_000_000) + " ms.");
        System.out.println("Paralle was " + timeSequental / timeParallel + " times faster");
    }
}
