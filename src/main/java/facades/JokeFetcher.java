package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ChuckDTO;
import dto.DadDTO;
import dto.JokesDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.HttpUtils;

public class JokeFetcher {

    class Joke implements Callable<String> {

        String url;

        Joke(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            String raw = HttpUtils.fetchData(url);
            return raw;
        }
    }

    public JokesDTO runParrallel() throws InterruptedException, ExecutionException {
        String[] hostList = {"https://api.chucknorris.io/jokes/random", "https://icanhazdadjoke.com"};
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();

        for (String url : hostList) {
            Callable<String> urlTask = new Joke(url);
            Future future = executor.submit(urlTask);
            futures.add(future);
        }

        ChuckDTO chuck = null;
        DadDTO dad = null;
        for (Future<String> future : futures) {
            if (future.get().contains("url")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                chuck = gson.fromJson(future.get(), ChuckDTO.class);
            } else {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                dad = gson.fromJson(future.get(), DadDTO.class);
            }
        }

        dad.setUrl("https://icanhazdadjoke.com");
        JokesDTO jokes = new JokesDTO(chuck, dad);

        return jokes;
    }

    public static String fetchJokes() throws InterruptedException, ExecutionException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JokeFetcher jf = new JokeFetcher();
        JokesDTO jokes = jf.runParrallel();
        String combinedJSON = gson.toJson(jokes);

        return combinedJSON;
    }

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        String chuckRaw = HttpUtils.fetchData("https://api.chucknorris.io/jokes/random");
        String dad = HttpUtils.fetchData("https://icanhazdadjoke.com");
        ChuckDTO chuckDto = gson.fromJson(chuckRaw, ChuckDTO.class);

        System.out.println("JSON fetched from chucknorris:");
        System.out.println(chuckDto);
        System.out.println("JSON fetched from dadjokes:");
        System.out.println(dad);

    }
}
