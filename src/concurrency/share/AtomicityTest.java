package concurrency.share;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在getValue方法前加synchronized关键字可解决并发访问问题
 */
public class AtomicityTest implements Runnable {
    private int i = 0;
    public int getValue() {
        return i;
    }
    private synchronized void evenIncrement() {
        i++;
        i++;
    }

    @Override
    public void run() {
        while (true) {
            evenIncrement();
        }
    }
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        AtomicityTest at = new AtomicityTest();
        exec.execute(at);
        while (true) {
            int val = at.getValue();
            if(val % 2 != 0) {
                System.out.println(val);
                System.exit(0);
            }
        }
    }
}
