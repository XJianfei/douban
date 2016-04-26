package x.douban.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import x.douban.R;
import x.douban.common.Subject;
import x.douban.utils.L;

/**
 * Created by Peter on 16/4/25.
 */
public class SubjectFragment extends BaseFragment {
    private ViewPager mSubjectViewPager = null;
    private ViewGroup mView = null;
    private Toolbar mSubejctToolbar = null;
    private int mType;
    private String mTitle = "";
    public SubjectFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.subject, container, false);
        if (vg == null) return null;
        mSubjectViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) vg.findViewById(R.id.subject_tab);
        if (tabLayout != null && mSubjectViewPager != null) {
            tabLayout.setupWithViewPager(mSubjectViewPager);
        }
        Bundle bundle = getArguments();
        mSubejctToolbar = (Toolbar) vg.findViewById(R.id.toolbar);
        if (bundle != null) {
            mType = bundle.getInt(Subject.BUNDLE_SUBJECT, Subject.SUBJECT_BOOK);
            mTitle = bundle.getString(Subject.BUNDLE_SUBJECT_TITLE, "");
            // TODO:
            mSubejctToolbar.setTitle("");
//            mSubejctToolbar.setTitle(mTitle);
        } else {
            L.warning("subjectfragment bundle is null");
        }
        mView = vg;
        return vg;
    }
}
