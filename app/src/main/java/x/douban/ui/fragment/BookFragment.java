package x.douban.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observer;
import rx.schedulers.Schedulers;
import x.douban.R;
import x.douban.model.Book;
import x.douban.service.DoubanService;
import x.douban.service.DoubanServiceImpl;
import x.douban.ui.activity.BookActivity;
import x.douban.ui.adpater.BookAdapter;
import x.douban.utils.L;
import x.douban.utils.MiscUtil;

/**
 * Created by Peter on 16/4/26.
 */
public class BookFragment extends BaseFragment {
    private ViewGroup mView = null;
    private RecyclerView mNewBookView = null;
    private BookAdapter mNewBookAdapter = null;
    private List<Book> mNewBooks = new ArrayList<>();

    private RecyclerView mPopularBookView = null;
    private BookAdapter mPopularBookAdapter = null;
    private List<Book> mPopularBooks = new ArrayList<>();

    private RecyclerView mHoteBookView = null;
    private BookAdapter mHoteBookAdapter = null;
    private List<Book> mHoteBooks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        BookAdapter.OnItemClickListener listener = new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View v, Book book) {
                L.dbg("click:" + book);
                if (book == null) {
                    L.error("click book is null");
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(
                    new ComponentName(getActivity(), BookActivity.class));
                intent.putExtra(BookActivity.BOOK_URL, book.url);
                intent.putExtra(BookActivity.BOOK_IMAGE, book.image);
                intent.putExtra(BookActivity.BOOK_TITLE, book.title);
//                startActivity(intent);
                ActivityCompat.startActivity(getActivity(), intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        Pair.create(v, getActivity().getResources().getString(R.string.transition_book_image))
                    ).toBundle());
            }
        };
        mView = (ViewGroup) inflater.inflate(R.layout.book_subject, container, false);
        mNewBookView = (RecyclerView) mView.findViewById(R.id.new_book_list);
        mNewBookAdapter = new BookAdapter(getContext(), mNewBooks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mNewBookView.setLayoutManager(linearLayoutManager);
        mNewBookView.setAdapter(mNewBookAdapter);
        mNewBookView.setNestedScrollingEnabled(false);
        mNewBookView.setHasFixedSize(false);
        mNewBookAdapter.setOnItemClickListener(listener);

        mPopularBookView = (RecyclerView) mView.findViewById(R.id.popular_book_list);
        mPopularBookAdapter = new BookAdapter(getContext(), mPopularBooks);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPopularBookView.setLayoutManager(linearLayoutManager);
        mPopularBookView.setAdapter(mPopularBookAdapter);
        mPopularBookView.setNestedScrollingEnabled(false);
        mPopularBookView.setHasFixedSize(false);
        mPopularBookAdapter.setOnItemClickListener(listener);

        mHoteBookView = (RecyclerView) mView.findViewById(R.id.hot_ebook_list);
        mHoteBookAdapter = new BookAdapter(getContext(), mHoteBooks);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHoteBookView.setLayoutManager(linearLayoutManager);
        mHoteBookView.setAdapter(mHoteBookAdapter);
        mHoteBookView.setNestedScrollingEnabled(false);
        mHoteBookView.setHasFixedSize(false);
        mHoteBookAdapter.setOnItemClickListener(listener);

        DoubanService mDoubanService = DoubanServiceImpl.getService();
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
                            mNewBooks.addAll(Book.parseFromIndex(es));
                            mNewBookView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNewBookAdapter.notifyDataSetChanged();
                                }
                            });

                            es = doc.select(".popular-books .list-col li");
                            mPopularBooks.addAll(Book.parseFromIndex(es));
                            mPopularBookView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPopularBookAdapter.notifyDataSetChanged();
                                }
                            });

                            es = doc.select(".ebook-area .list-col li");
                            mHoteBooks.addAll(Book.parseFromIndex(es));
                            mHoteBookView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mHoteBookAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (IOException e) {
                            L.error(MiscUtil.getStackTrace(e));
                        }
                }
            });
        return mView;
    }
}
