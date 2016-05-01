package x.douban.utils;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;

import javax.xml.transform.Source;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import x.douban.gson.Tag;
import x.douban.model.Book;
import x.douban.model.Book_Table;
import x.douban.service.DoubanService;
import x.douban.service.DoubanServiceImpl;
import x.rxcache.BaseCacheObservable;
import x.rxcache.CacheObservable;
import x.rxcache.Data;
import x.rxcache.MemoryCacheObservable;
import x.rxcache.XObservable;

/**
 * Created by Peter on 16/5/1.
 */
public class RxPagerLoader {
    public static final String TAG = "RxBookLoader";
    private static Context mContext = null;
    private static Source source = null;

    public static void init(Context context) {
        mContext = context;
    }

    static class DiskCache extends BaseCacheObservable {
        public static CacheObservable create(String id) {
            return BaseCacheObservable.create(id)
                .setFetch(new Fetch() {
                    @Override
                    public Data fetch(String key) {
                        String info = "";
                        try {
                            info = MiscUtil.readFromFile(key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Data data = new Data(info, key);
                        return data;
                    }
                })
                .setCache(new Cache() {
                    @Override
                    public Object cache(String key) {
                        Book book = SQLite.select().from(Book.class)
                            .where(Book_Table.id.eq(Integer.parseInt(key)))
                            .querySingle();
                        return book;
                    }

                    @Override
                    public void save(String key, Object value) {
                        if (value instanceof Book) {
                            Book book = (Book) value;
                            book.save();
                        } else if (value instanceof x.douban.gson.Book){
                            try {
                                gsonToModel((x.douban.gson.Book) value).save();
                            } catch (Exception e) {
                                L.error(MiscUtil.getStackTrace(e));
                            }
                        }
                    }
                });
        }
    }

    static class NetCache extends BaseCacheObservable {
        public static CacheObservable create(String id) {
            return BaseCacheObservable.create(id)
                .setFetch(new Fetch() {
                    @Override
                    public Data fetch(String key) {
                        DoubanService service = DoubanServiceImpl.getService();
                        final Data data = new Data(null, key);
                        service.book(Integer.parseInt(key))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(new Action1<x.douban.gson.Book>() {
                                @Override
                                public void call(x.douban.gson.Book book) {
                                    data.object = gsonToModel(book);
                                }
                            });
                        return data;
                    }
                });
        }
    }

    public static ConnectableObservable<Data> getLoaderObservable(final String id) {
        ConnectableObservable<Data> source;
        source = XObservable
            .addCaches(
                // 3 level
                MemoryCacheObservable.create(id, 0),
                // DiskCache
                DiskCache.create(id),
                // NetworkCache
                NetCache.create(id));
        return source;
    }

    public static Book gsonToModel(x.douban.gson.Book src) {
        Book book = new Book();
        book.setId(Integer.parseInt(src.getId()));
        book.author = "";
        for (String author : src.getAuthor()) {
            book.author += author + ",";
        }
        if (book.author != "")
            book.author = book.author.substring(0, book.author.length() - 1);
        book.alt = src.getAlt();
        book.alt_title = src.getAltTitle();
        book.author_intro = src.getAuthorIntro();
        book.average_rating = Float.parseFloat(src.getRating().getAverage());
        book.binding = src.getBinding();
        book.catalog = src.getCatalog();
        book.ebook_url = src.getUrl();
        book.image = src.getImage();
        book.isbn10 = src.getIsbn10();
        book.isbn13 = src.getIsbn13();
        book.num_rating = src.getRating().getNumRaters();
        book.origin_title = src.getOriginTitle();
        book.pages = Integer.parseInt(src.getPages());
        book.price = src.getPrice();
        book.pubdate = src.getPubdate();
        book.publisher = src.getPublisher();
//        Series series = src.getSeries();
//        if (series != null) {
//            book.series_id = src.getSeries().getId();
//            book.series_title = src.getSeries().getTitle();
//        }
        book.sub_title = src.getSubtitle();
        book.summary = src.getSummary();
        book.tags = "";
        for (Tag tag : src.getTags()) {
            book.tags += tag.getName() + ",";
        }
        if (book.tags != "")
            book.tags = book.tags.substring(0, book.tags.length() - 1);
        book.title = src.getTitle();
        book.translator = "";
        for (String s : src.getTranslator()) {
            book.translator += s + ",";
        }
        if (book.translator != "") {
            book.translator = book.translator.substring(0, book.translator.length() - 1);
        }
        book.url = src.getUrl();
        return book;
    }
}
