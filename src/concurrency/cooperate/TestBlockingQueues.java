package concurrency.cooperate;
import	java.io.InputStreamReader;
import	java.io.BufferedReader;

import concurrency.basic.LiftOff;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

class LiftOffRunner implements Runnable {
    private BlockingQueue<LiftOff> rockets;
    public LiftOffRunner(BlockingQueue<LiftOff> queue) {
        rockets = queue;
    }
    public void add(LiftOff lo) {
        try {
            rockets.put(lo);
        } catch (InterruptedException e) {
            System.out.println("Interrupted during put()");
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                LiftOff rocket = rockets.take();
                rocket.run();
            }
        } catch (InterruptedException e) {
            System.out.println("Waking from take()");
        }
        System.out.println("Exiting LiftOffRunner");
    }
}
public class TestBlockingQueues {
    static void getkey() {
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void getkey(String message) {
        System.out.println(message);
        getkey();
    }
    static void test(String msg, BlockingQueue<LiftOff> queue) {
        System.out.println(msg);
        LiftOffRunner runner = new LiftOffRunner(queue); // 消费者
        Thread t = new Thread(runner);
        t.start();
        for (int i = 0; i < 5; i++) {
            runner.add(new LiftOff(5)); // 生产者
        }
        getkey("Press 'Enter' (" + msg + ")");
        t.interrupt();
    }
    public static void main(String [] args) {
        test("LinkedBlockingQueue", new LinkedBlockingQueue<LiftOff>());
        test("ArrayBlockingQueue", new ArrayBlockingQueue<LiftOff>(3));
        test("SynchronousQueue", new SynchronousQueue<LiftOff>()); // size of 1
    }
}
