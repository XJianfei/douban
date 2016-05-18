package x.douban.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 16/5/18.
 */
public class Comment {
    public String name;
    public String date;
    public String avatar;
    public int rating;
    public String content;

    public static List<Comment> parseHotComment(Elements elements) {
        List<Comment> comments = new ArrayList<>();
        if (elements == null) return comments;
        Comment comment;
        for (Element element : elements) {
            comment = new Comment();
            comment.avatar = "";
            Element e = element.select("h3 .comment-info a").first();
            if (e != null) {
                comment.name = e.text();
            }
            e = element.select("h3 .comment-info span:not(.rating)").first();
            if (e != null) {
                comment.date = e.text();
            }
            e = element.select("h3 .comment-info .user-stars").first();
            if (e != null) {
                String[] classes = (String[]) e.classNames().toArray();
                for (String str : classes) {
                    if (str.contains("allstart")) {
                        try {
                            comment.rating = Integer.parseInt(str.replace("allstart", ""));
                        } catch (Exception ep) {

                        }
                    }
                }
            }
            e = element.select(".comment-content").first();
            if (e != null) {
                comment.content = e.text();
            }
            comments.add(comment);
        }
        return comments;
    }
}
