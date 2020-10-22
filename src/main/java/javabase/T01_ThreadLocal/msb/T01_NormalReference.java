package javabase.T01_ThreadLocal.msb;

import java.io.IOException;

/**
 * @Auther: weiliang
 * @Date: 2020/10/22 12:09
 * @Description:
 */
public class T01_NormalReference {
    public static void main(String[] args) throws IOException {
        M m = new M();
        m = null;
        System.gc();
        System.in.read();
    }
}
