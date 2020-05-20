package javabase;

import java.util.concurrent.locks.ReentrantLock;

public class T03_AQS {
    public static void main(String[] args) {
        ReentrantLock rl = new ReentrantLock();
        rl.lock();
    }
}
