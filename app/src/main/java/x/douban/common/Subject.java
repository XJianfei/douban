package x.douban.common;

/**
 * Created by Peter on 16/4/26.
 */
public class Subject {
    public final static int SUBJECT_BOOK = 0;
    public final static int SUBJECT_MOVIE = 1;
    public final static int SUBJECT_MUSIC = 3;
    public final static int SUBJECT_EVENT = 4;
    public final static String BUNDLE_SUBJECT = "subject_type";
    public final static String BUNDLE_SUBJECT_TITLE = "subject_title";
    public int type;
    public String title = "";
    public Subject(int type, String title) {
        this.type = type;
        this.title = title;
    }
}
