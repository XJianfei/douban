package x.douban.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import x.douban.R;
import x.douban.common.Book;
import x.douban.common.Subject;
import x.douban.service.DoubanService;
import x.douban.service.DoubanServiceImpl;
import x.douban.utils.L;
import x.douban.utils.MiscUtil;

/**
 * Created by Peter on 16/4/25.
 */
public class SubjectFragment extends BaseFragment {
    private ViewPager mSubjectViewPager = null;
    private ViewGroup mView = null;
    private Toolbar mSubejctToolbar = null;
    private int mType;
    private String mTitle = "";
    private LinearLayout layout;
    public SubjectFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
        /*
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
        DoubanService mDoubanService = DoubanServiceImpl.getService();
        layout = (LinearLayout) vg.findViewById(R.id.content);
        mDoubanService.bookIndex()
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<Response<ResponseBody>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    L.error(MiscUtil.getStackTrace(e));
                }

                @Override
                public void onNext(Response<ResponseBody> responseBodyResponse) {
                    if (responseBodyResponse.body() != null)
                        try {
                            String str = responseBodyResponse.body().string();
                            Document doc = Jsoup.parse(str);
                            // new books
                            Elements es = doc.select(".books-express .slide-item li");
                            L.dbg("Count:" + es.size());
                            List<Book> books = Book.parseFromIndex(es);
                            List<TextView> tvs = new ArrayList<TextView>();
                            for (Book b : books) {
                                L.dbg(""+b);
                            }
                            if (layout != null) {
                                Observable.from(books)
                                    .map(new Func1<Book, TextView>() {
                                        @Override
                                        public TextView call(Book book) {
                                            TextView tv = new TextView(getContext());
                                            tv.setText(book.toString());
                                            return tv;
                                        }
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<TextView>() {
                                        @Override
                                        public void call(TextView textView) {
                                            layout.addView(textView);
                                        }
                                    });
                            }
                        } catch (IOException e) {
                            L.error(MiscUtil.getStackTrace(e));
                        }
                }
            });
        mView = vg;
        return vg;
        */
    }
}
