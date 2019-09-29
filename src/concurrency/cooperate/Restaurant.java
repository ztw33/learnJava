package concurrency.cooperate;
import	java.util.concurrent.Executors;
import	java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

class Meal {
    private final int orderNum;
    public Meal(int orderNum) {
        this.orderNum = orderNum;
    }
    public String toString() {
        return "Meal " + orderNum;
    }
}

class WaitPerson implements Runnable {
    private Restaurant restaurant;
    public WaitPerson(Restaurant r) {
        restaurant = r;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                synchronized (this) { // wait方法需要在同步块里
                    while (restaurant.meal == null)
                        wait();
                }
                System.out.println("WaitPerson got " + restaurant.meal);
                synchronized (restaurant.chef) { // notify方法需要在同步块里
                    restaurant.meal = null;
                    restaurant.chef.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("WaitPerson interrupted");
        }
    }
}

class Chef implements Runnable {
    private Restaurant restaurant;
    private int count = 0;
    public Chef(Restaurant r) { restaurant = r; }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (restaurant.meal != null)
                        wait();
                }
                if(++count == 10) {
                    System.out.println("Out of food, closing");
                    restaurant.exec.shutdownNow();
                }
                System.out.println("Order up! ");
                synchronized (restaurant.waitPerson) {
                    restaurant.meal = new Meal(count);
                    restaurant.waitPerson.notifyAll();
                }
                /*
                线程池执行shutdownNow方法后，此线程并不马上抛出异常，而是在试图进入一个（可中断的）阻塞操作时抛出异常（即下面的sleep方法）。所以中断后会首先看到"Order up!"再到catch块中
                 */
                TimeUnit.MILLISECONDS.sleep(100); // 如果移除sleep方法，此任务在调用shutdownNow之后将回到循环顶部，并由于Thread.interrupted()测试退出循环，并不抛出异常
            }
        } catch (InterruptedException e) {
            System.out.println("Chef interrupted");
        }
    }
}

public class Restaurant {
    Meal meal;
    ExecutorService exec = Executors.newCachedThreadPool();
    WaitPerson waitPerson = new WaitPerson(this);
    Chef chef = new Chef(this);
    public Restaurant() {
        exec.execute(chef);
        exec.execute(waitPerson);
    }
    public static void main(String [] args) {
        new Restaurant();
    }
}
