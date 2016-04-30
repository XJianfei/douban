package x.douban.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 16/4/30.
 */
@Table(database = XDataBase.class, name = "book")
public class Book extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column(name = "isbn10", length = 10)
    public String isbn10;
    @Column(name = "isbn13", length = 13)
    public String isbn13;
    @Column
    public String title;
    @Column
    public String origin_title;
    @Column
    public String alt_title;
    @Column
    public String sub_title;
    @Column
    public String url;
    @Column
    public String alt;
    @Column
    public String image;
    @Column
    public String author;
    @Column
    public String translator;
    @Column
    public String publisher;
    @Column
    public String pubdate;
    @Column
    public int num_rating;
    @Column
    public int average_rating;
    @Column
    public String tags;
    @Column
    public String binding;
    @Column
    public float price;
    @Column
    public String series_id;
    @Column
    public String series_title;
    @Column(length = 5)
    public int pages;
    @Column
    public String author_intro;
    @Column
    public String summary;
    @Column
    public String catalog;
    @Column
    public String ebook_url;
    @Column
    public float ebook_price;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book() {
    }

    ;

    public Book(int id, String title, String author, String url, String summary,
                String image, String publisher, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.url = url;
        this.summary = summary;
        this.image = image;
        this.publisher = publisher;
        this.pubdate = date;
    }

    public static List<Book> parseFromIndex(Elements elements) {
        List<Book> books = new ArrayList<>();
        Book book;
        for (Element element : elements) {
            book = new Book();
            Element e = element.select(".cover a").first();
            if (e != null) {
                book.url = e.attr("href");
            }
            e = element.select(".cover img").first();
            if (e != null) {
                book.image = e.attr("src");
            }
            e = element.select(".info .title a").first();
            if (e != null) {
                book.title = e.text();
            }
            e = element.select(".info .author").first();
            if (e != null) {
                book.author = e.text();
            }
            e = element.select(".info .year").first();
            if (e != null) {
                book.pubdate = e.text();
            }
            e = element.select(".info .publisher").first();
            if (e != null) {
                book.publisher = e.text();
            }
            e = element.select(".info .abstract").first();
            if (e != null) {
                book.summary = e.text();
            }
            books.add(book);
        }
        return books;
    }

    @Override
    public String toString() {
        return "" + title + "@" + author;
    }
}
