import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class Main {

    private static final String API_KEY = "25d7d610d23b43c0896ca29b34c25e75";

    public static void main(String[] args) throws Exception {

        String uri = "https://api.assemblyai.com/v2/transcript";
        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://bit.ly/3yxKEIY");
        Gson gson = new Gson();
        String postBody = gson.toJson(transcript);
        //System.out.println(postBody);
        HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("authorization", API_KEY)
                    .POST(BodyPublishers.ofString(postBody))
                    .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        //System.out.println(transcript.getId());
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(uri + "/" + transcript.getId()))
                .header("authorization", API_KEY)
                .build();

        while (true) {
            System.out.println("processing...");
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }
            Thread.sleep(1000);
        }

        System.out.println("Transcription completed.");
        System.out.println(transcript.getText());
    }
}