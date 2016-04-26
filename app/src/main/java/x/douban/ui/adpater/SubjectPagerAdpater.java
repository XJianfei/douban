package x.douban.ui.adpater;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import x.douban.common.Subject;
import x.douban.ui.fragment.SubjectFragment;

/**
 * Created by Peter on 16/4/25.
 */
public class SubjectPagerAdpater extends FragmentPagerAdapter {
    private List<SubjectFragment> mFragments = null;
    private List<Subject> mSubjects = null;
    private ViewPager mSubjectViewPager = null;
    private TabLayout mSubjectTabLayout = null;
    public SubjectPagerAdpater(FragmentManager fm, ViewPager vp, List<Subject> subjects) {
        super(fm);
        mSubjects = subjects;
        if (vp != null) {
            mSubjectViewPager = vp;
            mFragments = new ArrayList<>();
            SubjectFragment sf;
            Bundle bundle;
            for (Subject subject : subjects) {
                sf = new SubjectFragment();
                bundle = new Bundle();
                bundle.putInt(Subject.BUNDLE_SUBJECT, subject.type);
                bundle.putString(Subject.BUNDLE_SUBJECT_TITLE, subject.title);
                sf.setArguments(bundle);
                mFragments.add(sf);
            }

        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSubjects.get(position).title;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
