package x.rxcache;

import rx.Observable;

/**
 * Created by Peter on 16/4/22.
 */
public abstract class CacheObservable<T> {
    /**
     * real observable
     */
    public Observable<Data<T>> observable;
    public abstract void save(Data<T> data);
    public abstract T cache(String info);
    Fetch mFetch = null;
    public CacheObservable setFetch(Fetch fetch) {
        mFetch = fetch;
        return this;
    }
    public Fetch getFetch() { return mFetch; }
    Cache cache = null;
    public CacheObservable setCache(Cache cache) {
        this.cache = cache;
        return this;
    }
    public Cache getCache() { return this.cache; }

    public static interface Cache {
        public Object cache(String key);
        public void save(String key, Object value);
    }
    public static interface Fetch {
        public Data fetch(String key);
    }
}
