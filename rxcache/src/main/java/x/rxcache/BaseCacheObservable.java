package x.rxcache;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Peter on 16/5/1.
 */
public class BaseCacheObservable extends CacheObservable {
    public BaseCacheObservable() {}

    public static CacheObservable create(final String url) {
        final BaseCacheObservable instance = new BaseCacheObservable();
        Observable observable = Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                Data data = null;
                Fetch fetch = instance.getFetch();
                if (fetch != null) {
                    data = fetch.fetch(url);
                }
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        instance.observable = observable;
        return instance;
    }

    @Override
    public void save(Data data) {
        Cache cache = getCache();
        if (cache != null) {
            cache.save(data.info, data.object);
        }
    }

    @Override
    public Object cache(String info) {
        Cache cache = getCache();
        if (cache != null) {
            cache.cache(info);
        }
        return null;
    }
}
