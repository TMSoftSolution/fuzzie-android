package sg.com.fuzzie.android.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sg.com.fuzzie.android.BuildConfig;
import sg.com.fuzzie.android.FuzzieApplication_;
import sg.com.fuzzie.android.utils.network.ConnectivityInterceptor;
import sg.com.fuzzie.android.utils.Constants;

/**
 * Created by nurimanizam on 10/12/16.
 */

public class FuzzieAPI {

    private static FuzzieAPI ourInstance = new FuzzieAPI();
    private static FuzzieAPIService apiService;

    public static FuzzieAPI getInstance() {
        return ourInstance;
    }

    private FuzzieAPI() {

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120,TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-Platform", "android")
                                .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(logInterceptor)
                .addInterceptor(new ConnectivityInterceptor(FuzzieApplication_.getInstance().getContext()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(FuzzieAPIService.class);
    }

    public static FuzzieAPIService APIService() { return apiService; }
}
