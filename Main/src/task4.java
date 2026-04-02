import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Головний клас Task4
 */
public class task4 {

    public static void main(String[] args) {
        task4 task = new task4();
        task.run();
    }

    /**
     * Метод запуску програми
     */
    public void run() {
        // Factory Method для створення табличного view
        Viewable viewable = new ViewableResultExtended();
        TableViewExtended tableView = (TableViewExtended) viewable.getView();

        // Параметри користувача
        Scanner sc = new Scanner(System.in);
        System.out.print("Введіть ширину таблиці: ");
        int width = sc.nextInt();
        System.out.print("Введіть кількість чисел: ");
        int count = sc.nextInt();

        tableView.setTableWidth(width);
        tableView.setNumberCount(count);

        // Діалогове меню
        Menu menu = new Menu((View) tableView);
        menu.menu();
    }
}

/**
 * Меню для користувача
 */
class Menu {

    private View view;

    public Menu(View view) {
        this.view = view;
    }

    public void menu() {
        String command = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        do {
            try {
                System.out.println("\nВведіть команду:");
                System.out.print("'q' - вихід, 'v' - показати, 'g' - згенерувати, 's' - зберегти, 'r' - відновити: ");
                command = in.readLine();

                switch (command) {
                    case "q":
                        System.out.println("Вихід.");
                        break;
                    case "v":
                        view.viewShow();
                        break;
                    case "g":
                        view.viewInit();
                        view.viewShow();
                        break;
                    case "s":
                        view.viewSave();
                        break;
                    case "r":
                        view.viewRestore();
                        view.viewShow();
                        break;
                    default:
                        System.out.println("Невірна команда.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (!"q".equals(command));
    }
}

/**
 * Інтерфейс для View
 */
interface View {
    void viewShow();
    void viewInit();
    void viewSave() throws IOException;
    void viewRestore() throws Exception;
}

/**
 * Factory Method інтерфейс
 */
interface Viewable {
    View getView();
}

/**
 * Фабрика Task4
 */
class ViewableResultExtended implements Viewable {
    @Override
    public View getView() {
        return new TableViewExtended();
    }
}

/**
 * Клас для зберігання даних
 */
class NumberData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int number;
    private String octal;
    private String hex;
    private int octalCount;
    private int hexCount;

    private transient String tempInfo;

    public NumberData(int number) {
        this.number = number;
        this.tempInfo = "Тимчасова інформація";
    }

    public int getNumber() { return number; }
    public String getOctal() { return octal; }
    public String getHex() { return hex; }
    public int getOctalCount() { return octalCount; }
    public int getHexCount() { return hexCount; }

    public void setNumber(int number) { this.number = number; }
    public void setOctal(String octal) { this.octal = octal; }
    public void setHex(String hex) { this.hex = hex; }
    public void setOctalCount(int c) { this.octalCount = c; }
    public void setHexCount(int c) { this.hexCount = c; }
}

/**
 * Calculator з перевантаженням
 */
class Calculator {
    private NumberData data;

    public Calculator(NumberData data) { this.data = data; }

    // Базовий метод
    public void calculate() {
        int number = data.getNumber();
        data.setOctal(Integer.toOctalString(number));
        data.setHex(Integer.toHexString(number));
        data.setOctalCount(data.getOctal().length());
        data.setHexCount(data.getHex().length());
    }

    // Перевантажений метод для демонстрації overloading
    public void calculate(int number) {
        data.setNumber(number);
        calculate();
    }
}

/**
 * Табличний View
 */
class TableViewExtended implements View {

    private static final String FILE = "data.ser";

    protected ArrayList<NumberData> list;
    protected int tableWidth = 50;
    protected int numberCount = 5;

    public TableViewExtended() {
        list = new ArrayList<>();
    }

    public void setTableWidth(int width) { this.tableWidth = width; }
    public void setNumberCount(int count) { this.numberCount = count; }

    @Override
    public void viewInit() {
        list.clear();
        for (int i = 0; i < numberCount; i++) {
            int num = (int)(Math.random() * 1000);
            NumberData data = new NumberData(num);

            Calculator calc = new Calculator(data);
            if (i % 2 == 0) calc.calculate();
            else calc.calculate(num * 2);

            list.add(data);
        }
    }

    @Override
    public void viewShow() {
        System.out.println("\nРезультати у вигляді таблиці:");
        System.out.println("-".repeat(tableWidth));
        System.out.printf("| %-10s | %-10s | %-10s | %-10s |\n",
                "Число", "8-рична", "16-рична", "Довжина");
        System.out.println("-".repeat(tableWidth));

        for (NumberData d : list) {
            int length = String.valueOf(d.getNumber()).length();
            System.out.printf("| %-10d | %-10s | %-10s | %-10d |\n",
                    d.getNumber(), d.getOctal(), d.getHex(), length);
        }

        System.out.println("-".repeat(tableWidth));
    }

    @Override
    public void viewSave() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
        oos.writeObject(list);
        oos.close();
    }

    @Override
    public void viewRestore() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
        list = (ArrayList<NumberData>) ois.readObject();
        ois.close();
    }
}

/**
 * Тест Task4
 */
class Task4Test {
    public static void main(String[] args) throws Exception {
        TableViewExtended view = new TableViewExtended();
        view.setNumberCount(6);
        view.setTableWidth(60);

        System.out.println("Генерація даних:");
        view.viewInit();
        view.viewShow();

        System.out.println("Збереження даних:");
        view.viewSave();

        System.out.println("Відновлення даних:");
        view.viewRestore();
        view.viewShow();
    }
}
