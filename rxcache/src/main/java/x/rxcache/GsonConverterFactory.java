package x.rxcache;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Peter on 16/5/1.
 */
public class GsonConverterFactory extends Converter.Factory {
    private Gson gson = null;
    private GsonConverterFactory() {
        gson = new Gson();
    }

    @Override
    public Converter<?, ?> toTarget(Type type, String value) {
        return new GsonConverter<>(gson, type);
    }
}
