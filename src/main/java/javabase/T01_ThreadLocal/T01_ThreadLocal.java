package javabase.T01_ThreadLocal;

import java.util.concurrent.TimeUnit;

public class T01_ThreadLocal {

    static ThreadLocal<Person> tl = new ThreadLocal<Person>();

    public static void main(String[] args) {

        // 2、在这里线程sleep两秒后tl取出。
        // 运行结果，取不到值。因为ThreadLocal变量是线程独有的。
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(tl.get());
        }).start();

        // 1、在这里线程sleep一秒后tl放入一个person。
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // ThreadLocal的set方法详解：
            // 1、拿到当前线程的map。
            // 2、往map里面放<调用者=tl，对象=persion>       Entry(ThreadLocal<?> k, Object v)
            // 3、其中tl是一个weakReference。
            tl.set(new Person());
        }).start();
    }


    static class Person{
        String name="zhangsan";
    }


}
