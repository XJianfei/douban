package x.douban.ui.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import x.douban.R;
import x.douban.common.Book;
import x.douban.utils.L;
import x.rxcache.RxImageLoader;

/**
 * Created by Peter on 16/4/27.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private Context mContext = null;
    private List<Book> mBooks = null;
    public BookAdapter(Context context, List<Book> books) {
        mContext = context;
        mBooks = books;
    }
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = mBooks.get(position);
        RxImageLoader.loadImageToView(holder.image, book.image);
        holder.title.setText(book.title);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image = null;
        private TextView title = null;

        private void init(View view) {
            if (view == null) return;
            image = (ImageView) view.findViewById(R.id.book_image);
            title = (TextView) view.findViewById(R.id.book_title);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }
    }
}
