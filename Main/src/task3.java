import java.io.*;
import java.util.ArrayList;

/**
 * Головний клас запуску програми
 */
public class Task3 {

    public static void main(String[] args) {
        // Створення об'єкта через фабричний метод
        Menu menu = new Menu(new ViewableResult().getView());
        menu.menu();
    }
}

/**
 * Клас для роботи з меню користувача
 */
class Menu {

    private View view; // Інтерфейс для роботи з відображенням

    public Menu(View view) {
        this.view = view;
    }

    /**
     * Діалогове меню
     */
    public void menu() {
        String s = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        do {
            try {
                System.out.println("\nВведіть команду:");
                System.out.print("'q' - вихід, 'v' - показати, 'g' - згенерувати, 's' - зберегти, 'r' - відновити: ");
                s = in.readLine();

                switch (s) {
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

        } while (!s.equals("q"));
    }
}

/**
 * Інтерфейс для відображення результатів
 */
interface View {

    void viewShow();        // показ результатів

    void viewInit();        // ініціалізація (генерація даних)

    void viewSave() throws IOException;   // збереження

    void viewRestore() throws Exception;  // відновлення
}

/**
 * Інтерфейс фабрики (Factory Method)
 */
interface Viewable {
    View getView(); // створення об'єкта
}

/**
 * Клас-фабрика
 */
class ViewableResult implements Viewable {

    @Override
    public View getView() {
        return new ViewResult(); // створює конкретний об'єкт
    }
}

/**
 * Клас для зберігання даних
 */
class NumberData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int number;        // вихідне число
    private String octal;      // 8-ричне представлення
    private String hex;        // 16-ричне представлення

    private int octalCount;    // кількість цифр (8-рична)
    private int hexCount;      // кількість цифр (16-рична)

    private transient String tempInfo; // не серіалізується

    public NumberData(int number) {
        this.number = number;
        this.tempInfo = "Тимчасова інформація";
    }

    public int getNumber() { return number; }

    public String getOctal() { return octal; }
    public String getHex() { return hex; }

    public int getOctalCount() { return octalCount; }
    public int getHexCount() { return hexCount; }

    public void setOctal(String octal) { this.octal = octal; }
    public void setHex(String hex) { this.hex = hex; }

    public void setOctalCount(int c) { this.octalCount = c; }
    public void setHexCount(int c) { this.hexCount = c; }
}

/**
 * Клас для виконання обчислень (агрегування)
 */
class Calculator {

    private NumberData data; // посилання на дані

    public Calculator(NumberData data) {
        this.data = data;
    }

    /**
     * Виконує обчислення:
     * переводить число у 8 та 16 системи і рахує кількість цифр
     */
    public void calculate() {
        int number = data.getNumber();

        // Переведення у різні системи числення
        String octal = Integer.toOctalString(number);
        String hex = Integer.toHexString(number);

        // Запис результатів
        data.setOctal(octal);
        data.setHex(hex);

        // Підрахунок кількості цифр
        data.setOctalCount(octal.length());
        data.setHexCount(hex.length());
    }
}

/**
 * Основний клас логіки:
 * робота з колекцією та серіалізацією
 */
class ViewResult implements View {

    private static final String FILE = "data.ser";

    private ArrayList<NumberData> list = new ArrayList<>(); // колекція

    /**
     * Генерація випадкових даних
     */
    @Override
    public void viewInit() {
        list.clear();

        for (int i = 0; i < 5; i++) {
            int num = (int)(Math.random() * 1000);

            NumberData data = new NumberData(num);

            Calculator calc = new Calculator(data);
            calc.calculate();

            list.add(data);
        }
    }

    /**
     * Виведення результатів
     */
    @Override
    public void viewShow() {
        System.out.println("\nРезультати:");

        for (NumberData d : list) {
            System.out.println(
                "Число: " + d.getNumber() +
                " | 8-рична: " + d.getOctal() +
                " (" + d.getOctalCount() + ")" +
                " | 16-рична: " + d.getHex() +
                " (" + d.getHexCount() + ")"
            );
        }

        System.out.println("Кінець.");
    }

    /**
     * Збереження колекції у файл
     */
    @Override
    public void viewSave() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
        oos.writeObject(list);
        oos.close();
    }

    /**
     * Відновлення колекції з файлу
     */
    @Override
    public void viewRestore() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
        list = (ArrayList<NumberData>) ois.readObject();
        ois.close();
    }
}
