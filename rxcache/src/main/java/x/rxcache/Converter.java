package x.rxcache;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Peter on 16/5/1.
 */
public interface Converter<F, T> {
    T convert(F from) throws IOException;
    F reconvert(T to) throws IOException;

    abstract class Factory {
        public Converter<?, ?> toTarget(Type type, String value) {
            return null;
        }
    }
}
