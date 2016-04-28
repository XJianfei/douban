package x.douban.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
public class BookActivity extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener{
    public static final String BOOK_URL = "book_url";
    public static final String BOOK_IMAGE = "book_image";
    public static final String BOOK_TITLE = "book_title";
    private CollapsingToolbarLayout mCollapsingToolbarLayout = null;
    private Toolbar mBookToolbar = null;
    private TextView mBookTitleView = null;
    private String mTitle = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        String url = getIntent().getStringExtra(BOOK_URL);
        if (url == null) {
            L.error("book url null");
        }
        String imageUrl = getIntent().getStringExtra(BOOK_IMAGE);
        mTitle = getIntent().getStringExtra(BOOK_TITLE);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mBookToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBookTitleView = (TextView) findViewById(R.id.toolbar_title);
        mBookTitleView.setText(mTitle);
        final ImageView image = (ImageView) findViewById(R.id.image);
        final ImageView bookImage = (ImageView) findViewById(R.id.book_image);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.id_appbarlayout);
        appBarLayout.addOnOffsetChangedListener(this);
        ConnectableObservable co = RxImageLoader.getLoaderObservable(imageUrl);
            co.observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Data>() {
                @Override
                public void call(Data data) {
                    if (data.object instanceof Bitmap) {
                        Bitmap bm = (Bitmap) data.object;
                        bookImage.setImageBitmap(bm);
                        blur((Bitmap) data.object, image);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            int color = colorLightFromBitmap(bm);
                            mCollapsingToolbarLayout.setContentScrimColor(color);
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
    private int colorLightFromBitmap(Bitmap bitmap) {
        Palette palette = new Palette.Builder(bitmap).generate();
        if (palette != null)
            return palette.getVibrantColor(Color.BLACK);
        return Color.BLACK;
    }
    private Palette.Swatch colorSwatchFromBitmap(Bitmap bitmap) {
        Palette palette = new Palette.Builder(bitmap).generate();
        if (palette != null)
            return palette.getVibrantSwatch(); //(0xFF00B0FF);
        return null;
    }
    private int colorDarkFromBitmap(Bitmap bitmap) {
        Palette palette = new Palette.Builder(bitmap).generate();
        if (palette != null)
            return palette.getDarkMutedColor(0xFF00B0FF);
        return 0xFF00B0FF;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        if (percentage > 0.7) {
            mBookToolbar.setVisibility(View.VISIBLE);
            mBookToolbar.setAlpha(percentage);
        } else {
            mBookToolbar.setVisibility(View.INVISIBLE);
        }
    }
}
