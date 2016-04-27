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
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>
            implements View.OnClickListener {
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
        holder.root.setTag(book);
        holder.root.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    private OnItemClickListener listener = null;
    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClickListener(v, (Book) v.getTag());
        }
    }
    public interface OnItemClickListener {
        void onItemClickListener(View v, Book book);
    }
    public void setOnItemClickListener(OnItemClickListener l) {listener = l;}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image = null;
        private TextView title = null;
        private View root = null;

        private void init(View view) {
            if (view == null) return;
            root = view;
            image = (ImageView) view.findViewById(R.id.book_image);
            title = (TextView) view.findViewById(R.id.book_title);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }
    }
}
