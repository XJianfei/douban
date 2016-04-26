package x.douban.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import x.douban.R;
import x.douban.common.Subject;
import x.douban.ui.adpater.SubjectPagerAdpater;

/**
 * Created by Peter on 16/4/25.
 */
public class MainActivity extends BaseActivity {

    private ViewPager mSubjectView = null;
    private FragmentPagerAdapter mSubjectAdapter = null;
    private TabLayout mSubjectTab = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setEnterTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSubject();
    }

    private void initSubject(){
        mSubjectView = (ViewPager) findViewById(R.id.viewpager);
        List<Subject> subjects = new ArrayList<>();
        Subject subject = new Subject(Subject.SUBJECT_BOOK, getString(R.string.book));
        subjects.add(subject);
        subject = new Subject(Subject.SUBJECT_MOVIE, getString(R.string.movie));
        subjects.add(subject);
        subject = new Subject(Subject.SUBJECT_MUSIC, getString(R.string.music));
        subjects.add(subject);
        subject = new Subject(Subject.SUBJECT_EVENT, getString(R.string.event));
        subjects.add(subject);
        mSubjectAdapter = new SubjectPagerAdpater(getSupportFragmentManager(), mSubjectView, subjects);
        mSubjectView.setAdapter(mSubjectAdapter);
    }
}
