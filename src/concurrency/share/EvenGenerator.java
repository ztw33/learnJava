package concurrency.share;

public class EvenGenerator extends IntGenerator {
    private int currentEvenvalue = 0;
    public synchronized int next() {
        ++currentEvenvalue;
        Thread.yield();
        ++currentEvenvalue;
        return currentEvenvalue;
    }
    public static void main(String[] args) {
        EvenChecker.test(new EvenGenerator());
    }
}
