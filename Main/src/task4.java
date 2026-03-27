import java.io.*;
import java.util.*;

/**
 * Головний клас з меню (діалог з користувачем)
 */
public class task4 {

    private static List<NumberData> list = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
    }

    /**
     * Меню програми
     */
    public static void menu() {
        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1 - Ввести число");
            System.out.println("2 - Показати таблицю");
            System.out.println("3 - Зберегти");
            System.out.println("4 - Відновити");
            System.out.println("5 - Тест");
            System.out.println("0 - Вихід");
            System.out.print("> ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> input();
                case 2 -> show();
                case 3 -> save();
                case 4 -> load();
                case 5 -> test();
                case 0 -> {
                    System.out.println("Вихід...");
                    return;
                }
            }
        }
    }

    /**
     * Введення числа
     */
    public static void input() {
        System.out.print("Введіть число: ");
        String num = sc.next();

        NumberData data = new NumberData(num);
        Calculator calc = new Calculator(data);
        calc.calculate();

        list.add(data);
        System.out.println("Обчислено!");
    }

    /**
     * Вивід таблиці (Factory Method + поліморфізм)
     */
    public static void show() {
        System.out.print("Введіть ширину таблиці: ");
        int width = sc.nextInt();

        ViewFactory factory = new TableFactory(width);
        View view = factory.createView();

        view.show(list); // dynamic dispatch
    }

    /**
     * Збереження
     */
    public static void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.ser"))) {
            oos.writeObject(list);
            System.out.println("Збережено!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Відновлення
     */
    public static void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.ser"))) {
            list = (List<NumberData>) ois.readObject();
            System.out.println("Відновлено!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Простий тест
     */
    public static void test() {
        NumberData d = new NumberData("11888821");
        new Calculator(d).calculate();

        assert d.getCount1() == 3;
        assert d.getCount8() == 4;

        System.out.println("Тест OK!");
    }
}

////////////////////////////////////////////////////////////

/**
 * Клас даних (Serializable)
 */
class NumberData implements Serializable {
    private String number;
    private int count1;
    private int count8;

    private transient String temp = "temp"; // не серіалізується

    public NumberData(String number) {
        this.number = number;
    }

    public String getNumber() { return number; }
    public int getCount1() { return count1; }
    public int getCount8() { return count8; }

    public void setCount1(int c) { count1 = c; }
    public void setCount8(int c) { count8 = c; }
}

////////////////////////////////////////////////////////////

/**
 * Обчислення (агрегування)
 */
class Calculator {

    private NumberData data;

    public Calculator(NumberData data) {
        this.data = data;
    }

    public void calculate() {
        int c1 = 0, c8 = 0;

        for (char c : data.getNumber().toCharArray()) {
            if (c == '1') c1++;
            if (c == '8') c8++;
        }

        data.setCount1(c1);
        data.setCount8(c8);
    }
}

////////////////////////////////////////////////////////////
// 🔹 FACTORY METHOD + ПОЛІМОРФІЗМ
////////////////////////////////////////////////////////////

/**
 * Інтерфейс відображення
 */
interface View {
    void show(List<NumberData> list);
}

/**
 * Абстрактна фабрика
 */
interface ViewFactory {
    View createView();
}

/**
 * Конкретна фабрика (Factory Method)
 */
class TableFactory implements ViewFactory {

    private int width;

    public TableFactory(int width) {
        this.width = width;
    }

    @Override
    public View createView() {
        return new TableView(width);
    }
}

////////////////////////////////////////////////////////////

/**
 * Вивід у вигляді таблиці
 * Демонструє overriding, overloading, поліморфізм
 */
class TableView implements View {

    private int width;

    public TableView() {
        this.width = 30;
    }

    public TableView(int width) {
        this.width = width;
    }

    /**
     * Overloading
     */
    public void show(List<NumberData> list, int width) {
        this.width = width;
        show(list);
    }

    /**
     * Overriding + dynamic dispatch
     */
    @Override
    public void show(List<NumberData> list) {

        String format = "%-" + (width / 2) + "s | %5s | %5s\n";

        System.out.println("\n--- TABLE ---");
        System.out.printf(format, "Number", "1", "8");
        System.out.println("-".repeat(width));

        for (NumberData d : list) {
            System.out.printf(format,
                    d.getNumber(),
                    d.getCount1(),
                    d.getCount8());
        }

        System.out.println("-".repeat(width));
    }
}