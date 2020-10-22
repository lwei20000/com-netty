package javabase.T01_ThreadLocal.msb;

import java.lang.ref.WeakReference;
import java.sql.SQLOutput;

/**
 * @Auther: weiliang
 * @Date: 2020/10/22 11:57
 * @Description:
 * 定义：弱引用，一遇到gc就被回收
 * 场景：ThreadLocal 弱引用是为了解决某些地方的内存泄漏问题。
 */
public class T03_WeakReference {

    public static void main(String[] args) throws InterruptedException {
        WeakReference<M> m = new WeakReference<M>(new M());

        System.out.println();
        System.gc();
        System.out.println(m.get());

        // 1、新建tl
        // 2、tl包含一个静态内部类ThreadLocalMap
        // 3、ThreadLocalMap包含一个静态内部类Entry（Entry是一个弱引用：static class Entry extends WeakReference<ThreadLocal<?>>）
        ThreadLocal<M> tl = new ThreadLocal<M>();
        // set里面吧对象放进ThreadLocalMap（如果ThreadLocalMap不存在就createMap()创建）
        tl.set(new M());
        System.out.println(tl.get());
        //tl.remove();
        tl = null;
        System.gc();
        Thread.sleep(3000);
        System.out.println(tl.get());
    }
}
