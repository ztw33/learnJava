package concurrency.share;

/**
 * 在nextSerialNumber方法前加synchronized关键字可解决并发访问问题
 */
public class SerialNumberGenerator {
    private static volatile int serialNumber = 0;
    public static int nextSerialNumber() {
        return serialNumber++;
    }
}
