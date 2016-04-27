package x.douban.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import x.douban.R;
import x.douban.common.Book;
import x.douban.common.Subject;
import x.douban.service.DoubanService;
import x.douban.service.DoubanServiceImpl;
import x.douban.ui.adpater.SubjectPagerAdpater;
import x.douban.ui.fragment.BookFragment;
import x.douban.ui.fragment.BookHeadFragment;
import x.douban.ui.fragment.EventFragment;
import x.douban.ui.fragment.EventHeadFragment;
import x.douban.ui.fragment.MovieFragment;
import x.douban.ui.fragment.MovieHeadFragment;
import x.douban.ui.fragment.MusicFragment;
import x.douban.ui.fragment.MusicHeadFragment;
import x.douban.ui.fragment.SubjectFragment;
import x.douban.ui.widget.RippleTabLayout;
import x.douban.utils.L;
import x.douban.utils.MiscUtil;

/**
 * Created by Peter on 16/4/25.
 */
public class MainActivity extends BaseActivity {

    private RippleTabLayout mSubjectTab = null;
    private DoubanService mDoubanService = null;
    private CollapsingToolbarLayout mCollapsingToolbarLayout = null;

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSubject();
        mDoubanService = DoubanServiceImpl.getService();

    }

    private TabLayout.Tab mLastSubjectTab = null;

    private void setMainThemeColor(int color) {
        setScrimColor(color);
        mSubjectTab.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(color);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        final Window window = getWindow();
        int colorStatusFrom = window.getStatusBarColor();
        int colorStatusTo = color;
        ValueAnimator colorStatusAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusFrom, colorStatusTo);
        colorStatusAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                window.setStatusBarColor((int)animation.getAnimatedValue());
            }
        });
        colorStatusAnimation.setDuration(getResources().getInteger(R.integer.ripple_anim_duration));
        colorStatusAnimation.setStartDelay(0);
        colorStatusAnimation.start();
    }
    private final void setScrimColor(int color) {
        if (mCollapsingToolbarLayout != null)
            mCollapsingToolbarLayout.setContentScrimColor(color);
    }

    private void initSubject(){
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mSubjectTab = (RippleTabLayout) findViewById(R.id.subject_tab);
        FragmentCouple[] fragmentCouples = new FragmentCouple[4];
        fragmentCouples[0] = new FragmentCouple(getString(R.string.book), 0xFF00B0FF,
            new BookHeadFragment(), new BookFragment());
        fragmentCouples[1] = new FragmentCouple(getString(R.string.movie), 0xFF43A047,
            new MovieHeadFragment(), new MovieFragment());
        fragmentCouples[2] = new FragmentCouple(getString(R.string.music), 0xFF7B1FA2,
            new MusicHeadFragment(), new MusicFragment());
        fragmentCouples[3] = new FragmentCouple(getString(R.string.event), 0xFFE64A19,
            new EventHeadFragment(), new EventFragment());
        for (FragmentCouple fc : fragmentCouples) {
            mSubjectTab.addTab(mSubjectTab.newTab().setText(fc.title).setTag(fc));
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.subject_head, fragmentCouples[0].head).add(R.id.subject_content, fragmentCouples[0].bottom);
        ft.commit();

        mSubjectTab.setSelectedTabIndicatorColor(Color.WHITE);
        mSubjectTab.setBackgroundColor(fragmentCouples[0].color);
        mSubjectTab.setRealBackgroundColor(fragmentCouples[0].color);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setEnterTransition(new Explode());
        }
        setMainThemeColor(fragmentCouples[0].color);
        mSubjectTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                FragmentCouple fragmentCouple = (FragmentCouple) tab.getTag();
                setMainThemeColor(fragmentCouple.color);

                if (mLastSubjectTab == null || tab.getPosition() > mLastSubjectTab.getPosition()) {
                    ft.setCustomAnimations(
                        R.anim.fragment_slide_in_from_right,
                        R.anim.fragment_slide_out_to_left);
                } else {
                    ft.setCustomAnimations(
                        R.anim.fragment_slide_in_from_left,
                        R.anim.fragment_slide_out_to_right);
                }
                mLastSubjectTab = tab;
                for (Fragment f : mFragmentManager.getFragments()) {
                    ft.hide(f);
                }
                if (fragmentCouple.head.isAdded()) {
                    ft.show(fragmentCouple.head).show(fragmentCouple.bottom);
                } else {
                    ft.add(R.id.subject_head, fragmentCouple.head).add(R.id.subject_content, fragmentCouple.bottom);
                }
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private class FragmentCouple {
        public String title;
        public int color;
        public Fragment head;
        public Fragment bottom;
        public FragmentCouple(String title, int color, Fragment head, Fragment bottom) {
            this.title = title;
            this.color = color;
            this.head = head;
            this.bottom = bottom;
        }
    }
}
