package bot.eightball.utilities;

import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestManager {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient requestClient = getClient();

    private static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(30, TimeUnit.SECONDS);
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        return builder.build();
    }


    public static String makeRequestCall(Request request) {
        Call call = requestClient.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody body;
            String result = null;

            body = response.body();
            if (body != null) {
                result = Objects.requireNonNull(body.string());
                body.close();
            }

            response.close();
            return result;

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int makeRequestCallForCode(Request request) {
        Call call = requestClient.newCall(request);
        try {
            Response response = call.execute();
            response.close();
            return response.code();

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return 0;

    }


    public static CompletableFuture<String> asyncGetRequest(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            return makeRequestCall(request);
        });


    }

    public static CompletableFuture<InputStream> asyncPostGetBytes(URL url, String body) {
        String finalBody = body == null ? "" : body;

        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, finalBody))
                    .build();

            try {
                Response response = requestClient.newCall(request).execute();
                return Objects.requireNonNull(response.body()).byteStream();
            } catch (IOException | NullPointerException e) {
                return null;
            }

        });

    }

    public static CompletableFuture<InputStream> asyncGetBytes(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = requestClient.newCall(request).execute();
                return Objects.requireNonNull(response.body()).byteStream();
            } catch (IOException | NullPointerException e) {
                return null;
            }

        });

    }

    public static CompletableFuture<Integer> asyncPostRequest(URL url, String body, String authentication) {
        String finalBody = body == null ? "" : body;

        return CompletableFuture.supplyAsync(() -> {
            Request request = null;

            try {
                Request.Builder requestBuilder = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .post(RequestBody.create(JSON, finalBody));

                if (authentication != null) {
                    requestBuilder.addHeader("Authorization", authentication);
                }

                request = requestBuilder.build();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


            return makeRequestCallForCode(request);

        });
    }

}
