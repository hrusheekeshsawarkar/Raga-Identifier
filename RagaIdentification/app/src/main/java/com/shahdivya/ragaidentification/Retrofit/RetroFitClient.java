package com.shahdivya.ragaidentification.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient
{
    private static Retrofit retrofit;
    public static Retrofit getInstance()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(5, TimeUnit.MINUTES).writeTimeout(5,TimeUnit.MINUTES).readTimeout(5,TimeUnit.MINUTES).build();
        if (retrofit==null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.5:8000/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}