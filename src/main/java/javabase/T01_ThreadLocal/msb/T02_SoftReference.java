package javabase.T01_ThreadLocal.msb;

import java.lang.ref.SoftReference;

/**
 * @Auther: weiliang
 * @Date: 2020/10/22 14:16
 * @Description:
 * 定义：软引用，是在空间不够的时候，就吧它回收。
 * 场景：一般用于实现缓存
 * 运参：VM option:-Xmx20M
 */
public class T02_SoftReference {
    public static void main(String[] args) throws InterruptedException {
        SoftReference<byte[]> m = new SoftReference<byte[]>(new byte[1024*1024*10]); // 10M
        System.out.println(m.get());
        System.gc();

        Thread.sleep(500);
        System.out.println(m.get());

        byte[] b = new byte[1024*1024*15];
        System.out.println(m.get());
    }
}
