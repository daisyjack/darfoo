package com.darfoo.darfoolauncher.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zjh on 14-12-22.
 */
public class TempFile {
    public static void remove(File f) {
        f.delete();
    }

    public static File createFile(int kiloSize) throws IOException {
        FileOutputStream fos = null;
        try {
            long size = (long) (1024 * kiloSize);
            File f = File.createTempFile("qiniu_" + kiloSize + "k", "tmp");
            f.createNewFile();
            fos = new FileOutputStream(f);
            byte[] b = getByte();
            long s = 0;
            while (s < size) {
                int l = (int) Math.min(b.length, size - s);
                fos.write(b, 0, l);
                s += l;
            }
            fos.flush();
            return f;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static byte[] getByte() {
        byte[] b = new byte[1024 * 4];
        b[0] = 'A';
        for (int i = 1; i < 1024 * 4; i++) {
            b[i] = 'b';
        }
        b[1024 * 4 - 2] = '\r';
        b[1024 * 4 - 1] = '\n';
        return b;
    }
}
