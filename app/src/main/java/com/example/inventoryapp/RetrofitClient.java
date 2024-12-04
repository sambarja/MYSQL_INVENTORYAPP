package com.example.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2/myApi/public/"; // Change for your local server URL
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    // Constructor to initialize context
    public RetrofitClient(Context context) {
        // OkHttp client with an interceptor for adding Authorization header
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // Original request
                        Request original = chain.request();
                        Log.d("Retrofit", "Request URL: " + original.url());

                        // Fetch the auth token from SharedPreferences or any secure place
                        String authToken = getAuthToken(context);  // Get the token dynamically

                        if (authToken == null || authToken.isEmpty()) {
                            Log.d("RetrofitClient", "No auth token found.");
                            return chain.proceed(original); // Proceed without the token
                        }

                        // Add Authorization header
                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Authorization", "Bearer " + authToken)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        // Build Retrofit instance with Gson converter
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    // Singleton access method
    public static synchronized RetrofitClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RetrofitClient(context);
        }
        return mInstance;
    }

    // Method to return the API interface
    public Api getApi() {
        return retrofit.create(Api.class);
    }

    // Method to fetch the Auth Token from SharedPreferences
    private static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", "");  // "auth_token" is the key where you store your token
    }
}
