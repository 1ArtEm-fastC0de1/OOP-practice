import java.io.*;
import java.util.*;

/**
 * Головний клас task5
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

    public void run() {
        TableView view = new TableView();

        Scanner sc = new Scanner(System.in);
        System.out.print("Введіть ширину таблиці: ");
        view.setWidth(sc.nextInt());
        System.out.print("Введіть кількість чисел: ");
        view.setCount(sc.nextInt());

        Menu menu = new Menu(view);
        menu.menu();
    }
}

/* ================= MENU ================= */
class Menu {
    private TableView view;
    private CommandManager manager = CommandManager.getInstance();
    public Menu(TableView view) {
        this.view = view;
    }
    public void menu() {
        Scanner sc = new Scanner(System.in);
        String cmd;
        do {
            System.out.println("\nКоманди:");
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
                    System.out.println("Вихід.");
                    break;
                default:
                    System.out.println("Невірна команда.");
            }

        } while (!cmd.equals("q"));
    }
}

/* ================= COMMAND ================= */

interface Command {
    void execute();

    void undo();
}

/* ================= SINGLETON ================= */

class CommandManager {

    private static CommandManager instance;
    private Stack<Command> history = new Stack<>();

    private CommandManager() {
    }

    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }

    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            System.out.println("Немає що скасувати.");
        }
    }
}

/* ================= COMMANDS ================= */

class GenerateCommand implements Command {

    private TableView view;

    public GenerateCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        view.init();
        view.show();
    }

    public void undo() {
        view.clear();
        System.out.println("Undo генерації.");
    }
}

class ShowCommand implements Command {

    private TableView view;

    public ShowCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        view.show();
    }

    public void undo() {
        System.out.println("Undo перегляду неможливий.");
    }
}

class SaveCommand implements Command {

    private TableView view;

    public SaveCommand(TableView v) {
        this.view = v;
    }

    public void execute() {
        try {
            view.save();
            System.out.println("Збережено.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void undo() {
        System.out.println("Undo збереження неможливий.");
    }
}

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
        System.out.println("Undo відновлення неможливий.");
    }
}

/* ================= MACRO ================= */

class MacroCommand implements Command {

    private List<Command> list = new ArrayList<>();

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

/* ================= VIEW ================= */

interface View {
    void show();
}

/* ================= FACTORY ================= */

interface Viewable {
    View getView();
}

class ViewableResultExtended implements Viewable {
    public View getView() {
        return new TableView();
    }
}

/* ================= DATA ================= */

class NumberData implements Serializable {
    int number;
    String oct;
    String hex;

    public NumberData(int n) {
        number = n;
    }
}

/* ================= TABLE VIEW ================= */

class TableView implements View {

    private static final String FILE = "data.ser";

    private ArrayList<NumberData> list = new ArrayList<>();
    private int width = 50;
    private int count = 5;

    public void setWidth(int w) {
        width = w;
    }

    public void setCount(int c) {
        count = c;
    }

    public void init() {
        list.clear();
        for (int i = 0; i < count; i++) {
            int n = (int) (Math.random() * 1000);
            NumberData d = new NumberData(n);
            d.oct = Integer.toOctalString(n);
            d.hex = Integer.toHexString(n);
            list.add(d);
        }
    }

    public void show() {
        System.out.println("\nТаблиця:");
        System.out.println("-".repeat(width));
        for (NumberData d : list) {
            System.out.println(d.number + " | " + d.oct + " | " + d.hex);
        }
        System.out.println("-".repeat(width));
    }

    public void clear() {
        list.clear();
    }

    public void save() throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
        oos.writeObject(list);
        oos.close();
    }

    public void restore() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
        list = (ArrayList<NumberData>) ois.readObject();
        ois.close();
    }
}

/* ================= TEST ================= */

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
