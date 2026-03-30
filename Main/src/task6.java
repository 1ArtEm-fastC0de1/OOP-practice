import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Головний клас програми
 */
public class task6 {

    public static void main(String[] args) {
        View view = new ViewResult(20);
        Menu menu = new Menu();

        menu.add(new ViewCommand(view));
        menu.add(new GenerateCommand(view));
        menu.add(new ExecuteCommand(view));

        menu.execute();
    }
}

/**
 * Клас точки (x, y)
 */
class Item2d {
    private double x;
    private double y;

    public Item2d() {}

    public Item2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setY(double y) { 
        this.y = y;
    }

    @Override
    public String toString(){
        return String.format("(x=%.2f, y=%.4f)", x, y);
    }
}

/**
 * Інтерфейс View
 */
interface View {
    void viewInit();

    void viewShow();
}

/**
 * Реалізація View (колекція)
 */
class ViewResult implements View {

    private List<Item2d> items = new ArrayList<>();
    private int n;

    public ViewResult(int n) {
        for (int i = 0; i < n; i++) {
            items.add(new Item2d());
        }
    }

    public List<Item2d> getItems() {
        return items;
    }

    @Override
    public void viewInit() {
        items.clear();

        double step = 10.0;

        for (int i = 0; i <= 360; i += step) {
            double x = i;
            double y = Math.sin(Math.toRadians(x));
            items.add(new Item2d(x, y));
        }

        System.out.println("Generated " + items.size() + " items.");
    }

    @Override
    public void viewShow() {
        for (Item2d item : items) {
            System.out.println(item);
        }
    }
}

/**
 * Інтерфейс команди
 */
interface Command {
    void execute();
}

/**
 * Інтерфейс консольної команди
 */
interface ConsoleCommand extends Command {
    char getKey();
}

/**
 * Команда показу
 */
class ViewCommand implements ConsoleCommand {
    private View view;

    public ViewCommand(View view) {
        this.view = view;
    }

    public char getKey() {
        return 'v';
    }

    public String toString() {
        return "'v'iew";
    }

    public void execute() {
        view.viewShow();
    }
}

/**
 * Команда генерації
 */
class GenerateCommand implements ConsoleCommand {
    private View view;

    public GenerateCommand(View view) {
        this.view = view;
    }

    public char getKey() {
        return 'g';
    }

    public String toString() {
        return "'g'enerate";
    }

    public void execute() {
        view.viewInit();
        view.viewShow();
    }
}

/**
 * Черга задач (Worker Thread)
 */
class CommandQueue {

    private Queue<Command> queue = new LinkedList<>();
    private boolean running = true;

    public CommandQueue() {
        new Thread(new Worker()).start();
    }

    public synchronized void put(Command cmd) {
        queue.add(cmd);
        notify();
    }

    public synchronized Command take() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return queue.poll();
    }

    public void shutdown() {
        running = false;
    }

    /**
     * Потік-обробник
     */
    private class Worker implements Runnable {
        public void run() {
            while (running) {
                Command cmd = take();
                cmd.execute();
            }
        }
    }
}

/**
 * Команда MAX
 */
class MaxCommand implements Command {

    private ViewResult view;
    private int result = -1;
    private int progress = 0;

    public MaxCommand(ViewResult view) {
        this.view = view;
    }

    public boolean running() {
        return progress < 100;
    }

    public int getResult() {
        return result;
    }

    @Override
    public void execute() {
        System.out.println("Max started...");
        List<Item2d> list = view.getItems();

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getY() > list.get(result == -1 ? 0 : result).getY()) {
                result = i;
            }

            progress = i * 100 / list.size();

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {
            }
        }

        System.out.println("Max index: " + result);
        progress = 100;
    }
}

/**
 * Команда AVG
 */
class AvgCommand implements Command {

    private ViewResult view;
    private double result = 0;
    private int progress = 0;

    public AvgCommand(ViewResult view) {
        this.view = view;
    }

    public boolean running() {
        return progress < 100;
    }

    public double getResult() {
        return result;
    }

    @Override
    public void execute() {
        System.out.println("Avg started...");
        List<Item2d> list = view.getItems();

        for (int i = 0; i < list.size(); i++) {
            result += list.get(i).getY();
            progress = i * 100 / list.size();

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {
            }
        }

        result /= list.size();
        System.out.println("Avg: " + result);
        progress = 100;
    }
}

/**
 * Команда MIN/MAX
 */
class MinMaxCommand implements Command {

    private ViewResult view;
    private int min = -1;
    private int max = -1;
    private int progress = 0;

    public MinMaxCommand(ViewResult view) {
        this.view = view;
    }

    public boolean running() {
        return progress < 100;
    }

    @Override
    public void execute() {
        System.out.println("MinMax started...");
        List<Item2d> list = view.getItems();

        for (int i = 0; i < list.size(); i++) {

            double y = list.get(i).getY();

            if (y > 0 && (min == -1 || y < list.get(min).getY()))
                min = i;

            if (y < 0 && (max == -1 || y > list.get(max).getY()))
                max = i;

            progress = i * 100 / list.size();

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {
            }
        }

        System.out.println("Min positive index: " + min);
        System.out.println("Max negative index: " + max);

        progress = 100;
    }
}

/**
 * Команда запуску потоків
 */
class ExecuteCommand implements ConsoleCommand {

    private View view;

    public ExecuteCommand(View view) {
        this.view = view;
    }

    public char getKey() {
        return 'e';
    }

    public String toString() {
        return "'e'xecute";
    }

    public void execute() {

        ViewResult vr = (ViewResult) view;

        CommandQueue q1 = new CommandQueue();
        CommandQueue q2 = new CommandQueue();

        MaxCommand max = new MaxCommand(vr);
        AvgCommand avg = new AvgCommand(vr);
        MinMaxCommand minmax = new MinMaxCommand(vr);

        q1.put(minmax);
        q2.put(max);
        q2.put(avg);

        while (max.running() || avg.running() || minmax.running()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }

        q1.shutdown();
        q2.shutdown();

        System.out.println("All threads done.");
    }
}

/**
 * Меню (макрокоманда)
 */
class Menu {

    private List<ConsoleCommand> commands = new ArrayList<>();

    public void add(ConsoleCommand c) {
        commands.add(c);
    }

    public void execute() {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            for (ConsoleCommand c : commands) {
                System.out.println(c);
            }
            System.out.println("'q' - exit");

            char key = sc.next().charAt(0);

            if (key == 'q')
                break;

            boolean found = false;

            for (ConsoleCommand c : commands) {
                if (c.getKey() == key) {
                    c.execute();
                    found = true;
                }
            }

            if (!found)
                System.out.println("Wrong command");
        }
    }
}