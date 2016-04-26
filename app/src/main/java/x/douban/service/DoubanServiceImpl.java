package x.douban.service;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import x.douban.utils.L;

/**
 * Created by Peter on 16/4/26.
 */
public class DoubanServiceImpl {
    private static final String BASE_URL = "https://api.douban.com/v2/";
    private static final String USER_AGENT =
        "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private volatile static DoubanService service = null;
    private volatile static OkHttpClient client = null;
    private DoubanServiceImpl() {}

    public synchronized static DoubanService getService() {
        if (service == null) {
            client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    String xurl = chain.request().url().queryParameter("xurl");
                    Request newRequest;
                    if (xurl == null) {
                        newRequest = chain.request().newBuilder().addHeader("User-Agent", USER_AGENT).build();
                    } else {
                        newRequest = chain.request().newBuilder().addHeader("User-Agent", USER_AGENT).url(xurl).build();
                    }
                    L.dbg("new request:" + newRequest.url());
                    return chain.proceed(newRequest);
                }
            }).build();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

            service = retrofit.create(DoubanService.class);
        }
        return service;
    }
}
