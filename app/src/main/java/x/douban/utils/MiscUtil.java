package x.douban.utils;

import android.content.Context;
import android.os.StatFs;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

public class MiscUtil {
    public static String toMD5(String content) {
        MessageDigest md = null;
        String md5 = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(content.getBytes());
            byte[] digests = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");
            for (byte b : digests) {
                i = b;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            md5 = buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static boolean storeSerializable(Serializable s, String path) {
        boolean ret = false;
        FileOutputStream fs = null;
        ObjectOutputStream os = null;
        File dir = new File(path).getParentFile();
        dir.mkdirs();
        try {
            fs = new FileOutputStream(path);
            os = new ObjectOutputStream(fs);
            os.writeObject(s);
            ret = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fs != null)
                    fs.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static Serializable restoreSerializable(String path) {
        Serializable s = null;
        FileInputStream fis = null;
        ObjectInputStream oi = null;
        try {
            fis = new FileInputStream(path);
            oi = new ObjectInputStream(fis);
            s = (Serializable) oi.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (oi != null) try {
                oi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    public static boolean writeToFile(String path, String content) throws IOException {
        File file = new File(path);
        if (content == null) return false;
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs())
                return false;
        }

        OutputStream out = new FileOutputStream(file);
        out.write(content.getBytes());
        out.close();
        return true;
    }

    public static String readFromFile(String path) throws IOException {
        String content = null;
        BufferedReader in = new BufferedReader(new FileReader(path));
        StringBuffer buffer = new StringBuffer();
        do {
            content = in.readLine();
            buffer.append(content + "\n");
        } while (content != null);
        in.close();
        return buffer.toString();
    }


    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static long getDirAvailableByes(String path) {
        StatFs statfs = new StatFs(path);
        if (statfs == null) return -1;
        return statfs.getAvailableBytes();
    }

    public static long getDirBytes(String path) {
        long size = 0;
        File dir = new File(path);
        if (!dir.exists()) return -1;

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                size += getDirUsagedBytes(file.getAbsolutePath());
            } else {
                size += file.length();
            }
        }
        return size;
    }
    public static long getDirUsagedBytes(String path) {
        long size = 0;
        File dir = new File(path);
        if (!dir.exists()) return -1;

        final long blockSize = new StatFs(path).getBlockSizeLong();
        File[] files = dir.listFiles();
        long length;
        for (File file : files) {
            if (file.isDirectory()) {
                size += getDirUsagedBytes(file.getAbsolutePath());
            } else {
                length = file.length();
                length += blockSize - (length % blockSize);
                size += length;
            }
        }
        return size;
    }

    public static void sortFileByLastModified(File[] files) {
        Arrays.sort(files, new CompratorByLastModified());
    }

    private static class CompratorByLastModified implements
        Comparator<File> {
        @Override
        public int compare(File f1,
                           File f2) {
            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
        }
    }
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getAttrInteger(Context context, int attr) {
        TypedValue tv = new TypedValue();
        int value = 0;
        if (context.getTheme().resolveAttribute(attr, tv, true)) {
            value = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
        }
        return value;
    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
