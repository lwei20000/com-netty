package javabase;

import java.lang.ref.WeakReference;

public class T01_WeakReference {
    public static void main(String[] args) {
        java.lang.ref.WeakReference<M> m = new java.lang.ref.WeakReference<>(new M());

        // 只要碰到垃圾回收，弱应用就被回收。
        //
        System.out.println(m.get());
        System.gc();
        System.out.println(m.get());

    }

    // 静态内部类
    static class M {
    }
}
