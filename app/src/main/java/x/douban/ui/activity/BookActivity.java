package x.douban.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
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
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import org.w3c.dom.Text;

import io.techery.properratingbar.ProperRatingBar;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import x.douban.R;
import x.douban.model.Book;
import x.douban.service.DoubanService;
import x.douban.service.DoubanServiceImpl;
import x.douban.utils.L;
import x.douban.utils.MiscUtil;
import x.douban.utils.RxBookLoader;
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
    public static final String BOOK_ID = "book_id";
    private CollapsingToolbarLayout mCollapsingToolbarLayout = null;
    private Toolbar mBookToolbar = null;
    private TextView mBookTitleView = null;
    private String mTitle = "";
    private int id = 0;
    private ProperRatingBar mRatingBar = null;

    private class TextOnClickListener implements View.OnClickListener {
        private int defaultHeight = -1;
        private int marginHeight = -1;
        private int defaultMaxLine;
        public TextOnClickListener() {
            this.defaultHeight = defaultHeight;
            this.marginHeight = marginHeight;
            defaultMaxLine =
                getResources().getInteger(R.integer.book_summary_line);
        }
        @Override
        public void onClick(View v) {
            final TextView textView = (TextView) v;
            if (defaultHeight == -1) {
                defaultHeight = textView.getHeight();
                marginHeight = defaultHeight - textView.getLayout().getHeight();
            }
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            params.height = textView.getHeight();
            textView.setLayoutParams(params);
            int currentLine = textView.getLineCount();
            if (currentLine <= defaultMaxLine) {
                textView.setMaxLines(1000);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        int lastHeight = textView.getLayout().getHeight() + marginHeight;
                        ValueAnimator animator = ValueAnimator
                            .ofInt(textView.getHeight(), lastHeight);
//                                            animator.setInterpolator(new DecelerateInterpolator());
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(500);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ViewGroup.LayoutParams params = textView.getLayoutParams();
                                params.height = (int) animation.getAnimatedValue();
                                textView.setLayoutParams(params);
                            }
                        });
                        animator.start();
                    }
                });
            } else {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        ValueAnimator animator = ValueAnimator
                            .ofInt(textView.getHeight(), defaultHeight);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(500);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ViewGroup.LayoutParams params = textView.getLayoutParams();
                                params.height = (int) animation.getAnimatedValue();
                                textView.setLayoutParams(params);
                            }
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                textView.setMaxLines(defaultMaxLine);
                            }
                        });
                        animator.start();
                    }
                });
            }
        }
    }

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

        id = getIntent().getIntExtra(BOOK_ID, 0);
        if (id == 0) {
            try {
                String[] p = url.split("/");
                if (p.length > 4) {
                    id = Integer.parseInt(p[4]);
                }
            } catch (Exception e) {
                L.error(MiscUtil.getStackTrace(e));
            }
        }

        RxBookLoader.init(this);
        ConnectableObservable connectableObservable = RxBookLoader.getLoaderObservable("" + id);
        connectableObservable.subscribeOn(Schedulers.io())
            .subscribe(new Observer() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    L.error(MiscUtil.getStackTrace(e));
                }

                @Override
                public void onNext(Object o) {
                    if (o instanceof Data) {
                        Book book = (Book) ((Data) o).object;
                        if (book != null) {
                            TextView mSummaryView = ((TextView)findViewById(R.id.summary));
                            mSummaryView.setText(book.summary);
                            TextOnClickListener listener = new TextOnClickListener();
                            mSummaryView.setOnClickListener(listener);
                            TextView mCategoryView = ((TextView)findViewById(R.id.category));
                            mCategoryView.setText(book.catalog);
                            listener = new TextOnClickListener();
                            mCategoryView.setOnClickListener(listener);

                            mRatingBar = (ProperRatingBar) findViewById(R.id.rating);
                            L.dbg("R:" + book.average_rating);
                            mRatingBar.setRating((int) book.average_rating / 2);

                            TextView tv = (TextView) findViewById(R.id.rating_average);
                            tv.setText("" + book.average_rating);
                            tv = (TextView) findViewById(R.id.rating_count);
                            tv.setText("" + book.num_rating + "人评价");
                            tv = (TextView) findViewById(R.id.short_info);
                            tv.setText("" + book.author + "/" + book.publisher
                                + "/" + book.pubdate + "/" + book.pages + "页");
                        }
                    }
                }
            });
        connectableObservable.connect();

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
