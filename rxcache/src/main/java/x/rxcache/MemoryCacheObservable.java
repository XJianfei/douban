package x.rxcache;

import android.support.v4.util.LruCache;

import rx.Observable;
import rx.Subscriber;

public class MemoryCacheObservable<T> extends CacheObservable<T> {
    private static LruCache mMemoryCache = null;

    public LruCache<String, T> getLruCache() {
        return mMemoryCache;
    }
    public void setLruCache(LruCache<String, T> lc) {
        this.mMemoryCache = lc;
    }

    /**
     * Create MemoryCacheObservable
     * @param key  for Cache
     * @param size for Cache, <= 0 meants the default size, max memory / 8
     * @return Observable
     */
    public static CacheObservable create(final String key, int size) {
        final MemoryCacheObservable instance = new MemoryCacheObservable();
        instance.observable = Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                XObservable.dbg("memory call");
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new Data(mMemoryCache.get(key), key));
                    subscriber.onCompleted();
                }
            }
        });
        if (instance.getLruCache() == null) {
            int cacheSize;
            if (size <= 0) {
                int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                cacheSize = maxMemory / 8;
            } else {
                cacheSize = size;
            }
            instance.setLruCache(new LruCache<String, Object>(cacheSize));
        }
        return instance;
    }

    @Override
    public void save(Data data) {
        if (mMemoryCache !=  null) {
            mMemoryCache.put(data.info, data.object);
        }
    }

    public T cache(String url) {
        if (mMemoryCache ==  null)
            return null;
        return (T) mMemoryCache.get(url);
    }

}
