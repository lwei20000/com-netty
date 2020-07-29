package javabase;

import java.util.concurrent.locks.ReentrantLock;

public class T04_ReentrantLockTest {
    static ReentrantLock reentrantLock = new ReentrantLock();

    public static void testSync() {

        // 加锁
        reentrantLock.lock();

        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            // 解锁
            reentrantLock.unlock();
        }

    }



    public static void main(String[] args) {
        testSync();
    }
}
