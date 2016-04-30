package x.douban.model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Peter on 16/4/30.
 */
@Database(name = XDataBase.NAME, version = XDataBase.VERSION)
public class XDataBase {
    public static final String NAME = "XDatabase";
    public static final int VERSION = 1;
}
