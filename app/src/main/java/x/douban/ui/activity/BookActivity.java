package x.douban.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import x.douban.R;
import x.douban.utils.L;
import x.rxcache.Data;
import x.rxcache.RxImageLoader;

/**
 * Created by Peter on 16/4/27.
 */
public class BookActivity extends BaseActivity {
    public static final String BOOK_URL = "book_url";
    public static final String BOOK_IMAGE = "book_image";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        String url = getIntent().getStringExtra(BOOK_URL);
        if (url == null) {
            L.error("book url null");
        }
        String imageUrl = getIntent().getStringExtra(BOOK_IMAGE);
        final ImageView image = (ImageView) findViewById(R.id.image);
        final ImageView bookImage = (ImageView) findViewById(R.id.book_image);
//        RxImageLoader.loadImageToView(image, imageUrl);
        ConnectableObservable co = RxImageLoader.getLoaderObservable(imageUrl);
            co.observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Data>() {
                @Override
                public void call(Data data) {
                    if (data.object instanceof Bitmap) {
                        bookImage.setImageBitmap((Bitmap) data.object);
                        blur((Bitmap) data.object, image);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            int color = colorFromBitmap((Bitmap) data.object);
                            if (color != 0)
                                window.setStatusBarColor(color);
                            window.setEnterTransition(new Explode());
                        }
                    }
                }
            });
        co.connect();
    }
    private void blur(Bitmap bkg, ImageView view) {
        float radius = 6;
        Bitmap overlay = Bitmap.createBitmap(bkg.getWidth(), bkg.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(this);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, bkg);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setImageBitmap(overlay);
        rs.destroy();

    }
    private int colorFromBitmap(Bitmap bitmap) {
        Palette palette = new Palette.Builder(bitmap).generate();
        if (palette != null)
            return palette.getVibrantColor(0);
        return 0;
    }
}
