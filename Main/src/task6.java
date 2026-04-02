import java.util.*;
/**
 * Task6 — паралельна обробка + Worker Thread
 */
public class task6 {

    public static void main(String[] args) {
        TaskQueue queue = new TaskQueue();

        // запуск worker-потоку
        new Worker(queue).start();

        Scanner sc = new Scanner(System.in);
        System.out.print("Кількість чисел: ");
        int n = sc.nextInt();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add((int)(Math.random() * 1000));
        }

        System.out.println("Дані: " + list);

        // додаємо задачі в чергу
        queue.addTask(() -> findMin(list));
        queue.addTask(() -> findMax(list));
        queue.addTask(() -> findAverage(list));
        queue.addTask(() -> filterEven(list));
    }

    /* ================= ПАРАЛЕЛЬНА ОБРОБКА ================= */

    static void findMin(List<Integer> list) {
        int min = list.parallelStream().min(Integer::compare).get();
        System.out.println("Мінімум: " + min);
    }

    static void findMax(List<Integer> list) {
        int max = list.parallelStream().max(Integer::compare).get();
        System.out.println("Максимум: " + max);
    }

    static void findAverage(List<Integer> list) {
        double avg = list.parallelStream().mapToInt(i -> i).average().getAsDouble();
        System.out.println("Середнє: " + avg);
    }

    static void filterEven(List<Integer> list) {
        List<Integer> even = list.parallelStream()
                .filter(x -> x % 2 == 0)
                .toList();
        System.out.println("Парні: " + even);
    }
}

/* ================= WORKER THREAD ================= */

class TaskQueue {

    private final Queue<Runnable> tasks = new LinkedList<>();

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
        notify();
    }

    public synchronized Runnable getTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            wait();
        }
        return tasks.poll();
    }
}

/**
 * Worker — обробляє задачі з черги
 */
class Worker extends Thread {

    private TaskQueue queue;

    public Worker(TaskQueue q) {
        this.queue = q;
    }

    public void run() {
        while (true) {
            try {
                Runnable task = queue.getTask();
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
