package task6;
import java.util.*;

/**
 * Головний клас програми.
 * Демонструє використання шаблонів Command, Singleton та Worker Thread,
 * а також паралельну обробку колекції.
 */
public class task6 {

    public static void main(String[] args) {
        new task6().run();
    }

    /**
     * Метод запуску програми.
     * Ініціалізує View, чергу задач та Worker-потік,
     * запускає діалогове меню.
     */
    public void run() {
        TableView view = new TableView();

        TaskQueue queue = new TaskQueue();
        new Worker(queue).start();

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter count: ");
        view.setCount(sc.nextInt());

        Menu menu = new Menu(view, queue);
        menu.menu();
    }
}

/**
 * Клас діалогового меню.
 * Забезпечує взаємодію користувача з програмою.
 */
class Menu {
    private TableView view;
    private TaskQueue queue;
    private CommandManager manager = CommandManager.getInstance();

    public Menu(TableView v, TaskQueue q) {
        view = v;
        queue = q;
    }

    /**
     * Основний цикл меню.
     */
    public void menu() {
        Scanner sc = new Scanner(System.in);
        String cmd;

        do {
            System.out.println("\nCommands:");
            System.out.println("g - generate, v - view");
            System.out.println("min, max, avg, even");
            System.out.println("u - undo, q - exit");

            cmd = sc.nextLine();

            switch (cmd) {
                case "g":
                    manager.execute(new GenerateCommand(view));
                    break;
                case "v":
                    manager.execute(new ShowCommand(view));
                    break;
                case "min":
                    manager.execute(new MinCommand(view, queue));
                    break;
                case "max":
                    manager.execute(new MaxCommand(view, queue));
                    break;
                case "avg":
                    manager.execute(new AvgCommand(view, queue));
                    break;
                case "even":
                    manager.execute(new EvenCommand(view, queue));
                    break;
                case "u":
                    manager.undo();
                    break;
                case "q":
                    System.out.println("Exit.");
                    break;
                default:
                    System.out.println("Invalid.");
            }

        } while (!cmd.equals("q"));
    }
}

/**
 * Інтерфейс команди.
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Singleton-клас для управління командами.
 */
class CommandManager {
    private static CommandManager instance;
    private Stack<Command> history = new Stack<>();

    private CommandManager() {}

    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }

    /**
     * Виконує команду.
     */
    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    /**
     * Скасовує останню команду.
     */
    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}

/**
 * Команда генерації даних.
 */
class GenerateCommand implements Command {
    private TableView view;

    public GenerateCommand(TableView v) { view = v; }

    public void execute() {
        view.viewInit();
        view.show();
    }

    public void undo() {
        view.clear();
        System.out.println("Undo generate.");
    }
}

/**
 * Команда відображення даних.
 */
class ShowCommand implements Command {
    private TableView view;

    public ShowCommand(TableView v) { view = v; }

    public void execute() {
        view.show();
    }

    public void undo() {
        System.out.println("Undo show impossible.");
    }
}

/**
 * Команда пошуку мінімуму.
 */
class MinCommand implements Command {
    private TableView view;
    private TaskQueue queue;

    public MinCommand(TableView v, TaskQueue q) {
        view = v;
        queue = q;
    }

    public void execute() {
        queue.addTask(() -> {
            int min = view.getNumbers().parallelStream()
                    .min(Integer::compare)
                    .get();
            System.out.println("Min: " + min);
        });
    }

    public void undo() {
        System.out.println("Undo min impossible.");
    }
}

/**
 * Команда пошуку максимуму.
 */
class MaxCommand implements Command {
    private TableView view;
    private TaskQueue queue;

    public MaxCommand(TableView v, TaskQueue q) {
        view = v;
        queue = q;
    }
    public void execute() {
        queue.addTask(() -> {
            int max = view.getNumbers().parallelStream()
                    .max(Integer::compare)
                    .get();
            System.out.println("Max: " + max);
        });
    }
    public void undo() {
        System.out.println("Undo max impossible.");
    }
}

/**
 * Команда обчислення середнього значення.
 */
class AvgCommand implements Command {
    private TableView view;
    private TaskQueue queue;

    public AvgCommand(TableView v, TaskQueue q) {
        view = v;
        queue = q;
    }

    public void execute() {
        queue.addTask(() -> {
            double avg = view.getNumbers().parallelStream()
                    .mapToInt(i -> i)
                    .average()
                    .getAsDouble();
            System.out.println("Average: " + avg);
        });
    }

    public void undo() {
        System.out.println("Undo avg impossible.");
    }
}

/**
 * Команда відбору парних чисел.
 */
class EvenCommand implements Command {
    private TableView view;
    private TaskQueue queue;

    public EvenCommand(TableView v, TaskQueue q) {
        view = v;
        queue = q;
    }

    public void execute() {
        queue.addTask(() -> {
            List<Integer> even = view.getNumbers().parallelStream()
                    .filter(x -> x % 2 == 0)
                    .toList();
            System.out.println("Even: " + even);
        });
    }

    public void undo() {
        System.out.println("Undo filter impossible.");
    }
}

/**
 * Черга задач для Worker Thread.
 */
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
 * Worker Thread для обробки задач.
 */
class Worker extends Thread {
    private TaskQueue queue;

    public Worker(TaskQueue q) {
        queue = q;
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

/**
 * Клас даних.
 */
class NumberData {
    int number;

    public NumberData(int n) {
        number = n;
    }
}

/**
 * Клас для роботи з колекцією.
 */
class TableView {
    private ArrayList<NumberData> list = new ArrayList<>();
    private int count = 5;

    public void setCount(int c) {
        count = c;
    }

    /**
     * Генерація випадкових чисел.
     */
    public void viewInit() {
        list.clear();
        for (int i = 0; i < count; i++) {
            list.add(new NumberData((int)(Math.random() * 1000)));
        }
    }

    /**
     * Виведення даних.
     */
    public void show() {
        System.out.println("Data:");
        for (NumberData d : list) {
            System.out.print(d.number + " ");
        }
        System.out.println();
    }

    public void clear() {
        list.clear();
    }

    /**
     * Отримання списку чисел.
     */
    public List<Integer> getNumbers() {
        List<Integer> nums = new ArrayList<>();
        for (NumberData d : list)
            nums.add(d.number);
        return nums;
    }
}

/**
 * Тестовий клас.
 */
class Task6Test {
    public static void main(String[] args) {
        TableView view = new TableView();
        view.setCount(5);
        view.viewInit();

        TaskQueue queue = new TaskQueue();
        new Worker(queue).start();

        CommandManager m = CommandManager.getInstance();

        m.execute(new MinCommand(view, queue));
        m.execute(new MaxCommand(view, queue));
        m.execute(new AvgCommand(view, queue));
    }
}