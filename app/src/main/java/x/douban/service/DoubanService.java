package x.douban.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import x.douban.gson.Book;

/**
 * Created by Peter on 16/4/26.
 */
public interface DoubanService {
    @GET("https://book.douban.com/")
    Observable<Response<ResponseBody>> bookIndex();
    @GET("https://x/")
    Observable<Response<ResponseBody>> get(@Query("xurl") String url);
    @GET("/v2/book/{id}")
    Observable<Book> book(@Path("id") int id);
}
