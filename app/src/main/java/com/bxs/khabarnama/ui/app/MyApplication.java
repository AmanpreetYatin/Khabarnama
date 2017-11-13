package com.bxs.khabarnama.ui.app;

import android.app.Application;
import android.content.Context;



import com.bxs.khabarnama.ui.Utils.MyPreferenceManager;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyApplication extends Application {
    public static final String TAG = MyApplication.class
            .getSimpleName();
    private static MyApplication mInstance;
    private MyPreferenceManager pref;
    private ImageLoader mImageLoader;
    private HttpProxyCacheServer proxy;

    private Gson mGson;
    private Retrofit mRetrofit;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        initImageLoader(getApplicationContext());
    }



    public Gson getGson() {
        if (mGson == null) {
            mGson  = new Gson();
        }

        return mGson;
    }

    public Retrofit getRetrofitClient(Gson gson) {
        if (mRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor())
                    .build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("www.gogole.com")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return mRetrofit;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);


        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public static synchronized MyApplication getMyApplicationInstance() {
        return mInstance;
    }


}
