package x.rxcache;


import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by Peter on 16/5/1.
 */
public class GsonConverter<T> implements Converter<String, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final Type type;

    GsonConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }
    @Override
    public T convert(String from) throws IOException {
        return gson.fromJson(from, type);
    }

    @Override
    public String reconvert(T to) throws IOException {
        return gson.toJson(to);
    }
}
