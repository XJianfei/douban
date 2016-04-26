package x.douban.common;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import x.douban.utils.L;

/**
 * Created by Peter on 16/4/26.
 */
public class Book {
    public int id;
    public String title;
    public String author;
    public String url;
    public String abstracts;
    public String image;
    public String publisher;
    public String date;
    public Book() {};
    public Book(int id, String title, String author, String url, String abstracts,
                String image, String publisher, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.url = url;
        this.abstracts = abstracts;
        this.image = image;
        this.publisher = publisher;
        this.date = date;
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
                book.date = e.text();
            }
            e = element.select(".info .publisher").first();
            if (e != null) {
                book.publisher = e.text();
            }
            e = element.select(".info .abstract").first();
            if (e != null) {
                book.abstracts = e.text();
            }
            books.add(book);
        }
        return books;
    }

    @Override
    public String toString() {
        return "" + title + ":" + author;
    }
}
