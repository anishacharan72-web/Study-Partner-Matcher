package com.studymatcher.app.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client.
 * Attaches Firebase ID token as Bearer auth header on every request.
 * BASE_URL — change to your Cloud Run / App Engine URL for production.
 */
public class ApiClient {

    // Development: local Spring Boot server
    // Production: change to your App Engine / Cloud Run URL
    private static final String BASE_URL = "https://study-partner-diwakar.onrender.com/";

    private static ApiService instance;

    public static ApiService getInstance() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Synchronous token fetch — runs on background thread via Retrofit
                            try {
                                String token = com.google.android.gms.tasks.Tasks
                                        .await(user.getIdToken(false))
                                        .getToken();
                                Request request = original.newBuilder()
                                        .header("Authorization", "Bearer " + token)
                                        .build();
                                return chain.proceed(request);
                            } catch (Exception e) {
                                return chain.proceed(original);
                            }
                        }
                        return chain.proceed(original);
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }
}
