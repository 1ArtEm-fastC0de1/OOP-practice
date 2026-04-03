package task5;
import java.io.*;
import java.util.*;

/**
 * Головний клас програми.
 * Відповідає за ініціалізацію TableView та запуск меню.
 */
public class task5 {

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        task5 t = new task5();
        t.run();
    }

    /**
     * Метод запуску програми.
     * Ініціалізує TableView, зчитує параметри від користувача та викликає меню.
     */
    public void run() {
        TableView view = new TableView();

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter table width: ");
        view.setWidth(sc.nextInt());
        System.out.print("Enter number of values: ");
        view.setCount(sc.nextInt());

        Menu menu = new Menu(view);
        menu.menu();
    }
}

/**
 * Клас для організації меню користувача.
 * Забезпечує виконання команд через CommandManager.
 */
class Menu {
    private TableView view;
    private CommandManager manager = CommandManager.getInstance();

    /**
     * Конструктор.
     * @param view екземпляр TableView для роботи з даними
     */
    public Menu(TableView view) {
        this.view = view;
    }

    /**
     * Основний цикл меню.
     * Зчитує команди користувача і виконує відповідні дії.
     */
    public void menu() {
        Scanner sc = new Scanner(System.in);
        String cmd;
        do {
            System.out.println("\nCommands:");
            System.out.println("g - generate, v - view, s - save, r - restore");
            System.out.println("u - undo, m - macro, q - exit");
            cmd = sc.nextLine();

            switch (cmd) {
                case "g":
                    manager.execute(new GenerateCommand(view));
                    break;
                case "v":
                    manager.execute(new ShowCommand(view));
                    break;
                case "s":
                    manager.execute(new SaveCommand(view));
                    break;
                case "r":
                    manager.execute(new RestoreCommand(view));
                    break;
                case "u":
                    manager.undo();
                    break;
                case "m":
                    MacroCommand macro = new MacroCommand();
                    macro.add(new GenerateCommand(view));
                    macro.add(new ShowCommand(view));
                    manager.execute(macro);
                    break;
                case "q":
                    System.out.println("Exit.");
                    break;
                default:
                    System.out.println("Invalid command.");
            }

        } while (!cmd.equals("q"));
    }
}

/**
 * Інтерфейс команди.
 * Визначає методи execute() та undo() для реалізації патерну Command.
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Singleton-клас для керування виконанням команд.
 * Забезпечує історію виконаних команд для можливості undo.
 */
class CommandManager {
    private static CommandManager instance;
    private Stack<Command> history = new Stack<>();

    private CommandManager() {}

    /**
     * Отримання екземпляру Singleton.
     * @return екземпляр CommandManager
     */
    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }

    /**
     * Виконання команди та додавання її до історії.
     * @param cmd команда для виконання
     */
    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    /**
     * Скасування останньої виконаної команди.
     */
    public void undo() {
        if (!history.isEmpty()) {
            Command cmd = history.pop();
            cmd.undo();
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}

/**
 * Команда генерації даних у таблиці.
 */
class GenerateCommand implements Command {
    private TableView view;

    public GenerateCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        view.viewInit();
        view.show();
    }

    public void undo() {
        view.clear();
        System.out.println("Undo generation.");
    }
}

/**
 * Команда відображення даних у таблиці.
 */
class ShowCommand implements Command {
    private TableView view;

    public ShowCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        view.show();
    }

    public void undo() {
        System.out.println("Undo view not possible.");
    }
}

/**
 * Команда збереження даних у файл.
 */
class SaveCommand implements Command {
    private TableView view;

    public SaveCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        try {
            view.save();
            System.out.println("Saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void undo() {
        System.out.println("Undo save not possible.");
    }
}

/**
 * Команда відновлення даних із файлу.
 */
class RestoreCommand implements Command {
    private TableView view;

    public RestoreCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        try {
            view.restore();
            view.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void undo() {
        System.out.println("Undo restore not possible.");
    }
}

/**
 * Команда макросу, що виконує послідовність інших команд.
 */
class MacroCommand implements Command {
    private List<Command> list = new ArrayList<>();

    /**
     * Додавання команди до макросу.
     * @param c команда для додавання
     */
    public void add(Command c) {
        list.add(c);
    }

    public void execute() {
        for (Command c : list)
            c.execute();
    }

    public void undo() {
        for (int i = list.size() - 1; i >= 0; i--)
            list.get(i).undo();
    }
}

/**
 * Інтерфейс для відображення даних.
 */
interface View {
    void show();
    void viewInit();
}

/**
 * Клас для зберігання числових даних у таблиці.
 */
class NumberData implements Serializable {
    public int number;
    public String oct;
    public String hex;

    /**
     * Конструктор.
     * @param n число для збереження
     */
    public NumberData(int n) {
        this.number = n;
    }
}


/**
 * Клас для представлення таблиці даних.
 */
class TableView implements View {
    private static final String FILE = "data.ser";

    private ArrayList<NumberData> list = new ArrayList<>();
    private int width = 50;
    private int count = 5;

    /**
     * Встановлення ширини таблиці.
     * @param w ширина таблиці
     */
    public void setWidth(int w) {
        width = w;
    }

    /**
     * Встановлення кількості чисел у таблиці.
     * @param c кількість чисел
     */
    public void setCount(int c) {
        count = c;
    }

    /**
     * Ініціалізація даних таблиці випадковими числами.
     */
    @Override
    public void viewInit() {
        list.clear();
        for (int i = 0; i < count; i++) {
            int n = (int) (Math.random() * 1000);
            NumberData d = new NumberData(n);
            d.oct = Integer.toOctalString(n);
            d.hex = Integer.toHexString(n);
            list.add(d);
        }
    }

    /**
     * Відображення даних таблиці у консолі.
     */
    @Override
    public void show() {
        System.out.println("\nTable:");
        System.out.println("-".repeat(width));
        for (NumberData d : list) {
            System.out.println(d.number + " | " + d.oct + " | " + d.hex);
        }
        System.out.println("-".repeat(width));
    }

    /**
     * Очищення даних таблиці.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Збереження даних таблиці у файл.
     * @throws Exception у випадку помилки запису
     */
    public void save() throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(list);
        }
    }

    /**
     * Відновлення даних таблиці з файлу.
     * @throws Exception у випадку помилки читання
     */
    public void restore() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            list = (ArrayList<NumberData>) ois.readObject();
        }
    }
}

/**
 * Тестовий клас для перевірки генерації та відображення даних.
 */
class Task5Test {
    public static void main(String[] args) {
        TableView view = new TableView();
        view.setCount(3);

        CommandManager m = CommandManager.getInstance();

        m.execute(new GenerateCommand(view));
        m.execute(new ShowCommand(view));

        System.out.println("Undo:");
        m.undo();
    }
}
