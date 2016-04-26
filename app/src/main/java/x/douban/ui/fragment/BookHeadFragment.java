package x.douban.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import x.douban.R;

/**
 * Created by Peter on 16/4/26.
 */
public class BookHeadFragment extends BaseFragment {
    private ViewGroup mView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        mView = (ViewGroup) inflater.inflate(R.layout.subject, container, false);
        return mView;
    }
}
