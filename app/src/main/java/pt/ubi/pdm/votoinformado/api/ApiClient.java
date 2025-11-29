package pt.ubi.pdm.votoinformado.api;

import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String RENDER_URL = "https://api-votoinformado.onrender.com/";
    private static final String LOCAL_URL = "http://10.0.2.2:3000/"; // Localhost for Android Emulator
    private static final String LOCAL_IP_URL = "http://10.250.134.7:3000/"; // Local IP for Physical Device

    private static volatile ApiClient instance;
    private static final Object lock = new Object();
    private final Retrofit retrofit;
    private static String baseUrl;

    private ApiClient(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    private static boolean isUrlAvailable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(15000); // 15 seconds timeout for Render cold start
            connection.setReadTimeout(15000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 300);
        } catch (IOException e) {
            return false;
        }
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    CountDownLatch latch = new CountDownLatch(1);
                    new Thread(() -> {
                        if (isUrlAvailable(RENDER_URL)) {
                            baseUrl = RENDER_URL;
                        } else if (isUrlAvailable(LOCAL_IP_URL)) {
                            baseUrl = LOCAL_IP_URL;
                        } else {
                            baseUrl = LOCAL_URL;
                        }
                        Log.d("ApiClient", "Using base URL: " + baseUrl);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        
                        instance = new ApiClient(retrofit);
                        latch.countDown();
                    }).start();

                    try {
                        latch.await(); // Block until initialization is complete
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        // Handle error, maybe fallback to a default
                        if (instance == null) {
                           Log.e("ApiClient", "Initialization interrupted, falling back to local URL");
                           baseUrl = LOCAL_URL;
                           Retrofit retrofit = new Retrofit.Builder()
                                   .baseUrl(baseUrl)
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .build();
                           instance = new ApiClient(retrofit);
                        }
                    }
                }
            }
        }
        return instance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }

    public static String getBaseUrl() {
        if (instance == null) {
            getInstance(); // ensure initialization
        }
        return baseUrl;
    }
}
