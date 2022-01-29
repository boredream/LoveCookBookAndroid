package com.boredream.baseapplication.net;

import android.text.TextUtils;

import com.boredream.baseapplication.utils.TokenKeeper;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chunyangli on 2018/2/6.
 */
public class HttpRequest {

    private static volatile HttpRequest instance = null;

    public static HttpRequest getInstance() {
        if (instance == null) {
            synchronized (HttpRequest.class) {
                if (instance == null) {
                    instance = new HttpRequest();
                }
            }
        }
        return instance;
    }

    private String host;
    private OkHttpClient client;
    private Retrofit retrofit;
    private Retrofit dataStreamRetrofit;

    public String getHost() {
        return host;
    }

    public OkHttpClient getClient() {
        return client;
    }

    private HttpRequest() {
//        host = "http://10.32.10.36:8080/api/";
        host = "https://www.papikoala.cn/api/";

        Interceptor headerInterceptor = chain -> {
            Request.Builder builder = chain.request().newBuilder()
                    .addHeader("device", "ANDROID")
                    .addHeader("Content-Type", "application/json");

            String token = TokenKeeper.getSingleton().getToken();
            if(!TextUtils.isEmpty(token)){
                builder.addHeader("token", token);
            }

            Request request = builder.build();
            return chain.proceed(request);
        };

        int timeout = 20;

        // OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(), createTrustManager())
                .hostnameVerifier(createVerifier())
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS);
        builder.addInterceptor(headerInterceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create()) // gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // rxjava 响应式编程
                .client(builder.build())
                .build();

        OkHttpClient.Builder dataStreamBuilder = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(), createTrustManager())
                .hostnameVerifier(createVerifier())
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS);
        dataStreamRetrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create()) // gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // rxjava 响应式编程
                .client(dataStreamBuilder.build())
                .build();
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }

    public ApiService getDataStreamApiService() {
        return dataStreamRetrofit.create(ApiService.class);
    }

    private static HostnameVerifier createVerifier() {
        return (s, sslSession) -> true;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{createTrustManager()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            //
        }

        return ssfFactory;
    }

    private static X509TrustManager createTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
}
