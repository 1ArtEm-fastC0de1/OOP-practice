import java.io.*;
import java.util.ArrayList;

/**
 * Головний клас запуску програми
 */
public class task3 {

    public static void main(String[] args) {

        Viewable viewable;

        // Перемикач типу відображення
        int type = 1;

        if (type == 1) {
            viewable = new ViewableResult();
        } else {
            viewable = new ViewableShort();
        }

        Menu menu = new Menu(viewable.getView());
        menu.menu();
    }
}

/**
 * Клас меню (діалоговий режим)
 */
class Menu {

    private View view;

    public Menu(View view) {
        this.view = view;
    }

    public void menu() {
        String s = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        do {
            try {
                System.out.println("\nEnter command:");
                System.out.print("'q' - exit, 'v' - show, 'g' - generate, 's' - save, 'r' - restore: ");
                s = in.readLine();

                switch (s) {
                    case "q":
                        System.out.println("Exit.");
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
                        System.out.println("Invalid command.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (!s.equals("q"));
    }
}

/**
 * Інтерфейс відображення
 */
interface View {

    void viewShow();

    void viewInit();

    void viewSave() throws IOException;

    void viewRestore() throws Exception;
}

/**
 * Інтерфейс фабрики
 */
interface Viewable {
    View getView();
}

/**
 * Фабрика для повного відображення
 */
class ViewableResult implements Viewable {

    @Override
    public View getView() {
        return new ViewResult();
    }
}

/**
 * Фабрика для скороченого відображення
 */
class ViewableShort implements Viewable {

    @Override
    public View getView() {
        return new ViewShort();
    }
}

/**
 * Клас даних (Serializable)
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

    public void setOctal(String octal) { this.octal = octal; }
    public void setHex(String hex) { this.hex = hex; }

    public void setOctalCount(int c) { this.octalCount = c; }
    public void setHexCount(int c) { this.hexCount = c; }
}

/**
 * Клас обчислення
 */
class Calculator {

    private NumberData data;

    public Calculator(NumberData data) {
        this.data = data;
    }

    public void calculate() {
        int number = data.getNumber();

        String octal = Integer.toOctalString(number);
        String hex = Integer.toHexString(number);

        data.setOctal(octal);
        data.setHex(hex);

        data.setOctalCount(octal.length());
        data.setHexCount(hex.length());
    }
}

/**
 * Повне відображення (робота з колекцією)
 */
class ViewResult implements View {

    private static final String FILE = "data.ser";

    private ArrayList<NumberData> list = new ArrayList<>();

    @Override
    public void viewInit() {
        list.clear();

        for (int i = 0; i < 5; i++) {
            int num = (int)(Math.random() * 1000);

            NumberData data = new NumberData(num);
            new Calculator(data).calculate();

            list.add(data);
        }
    }

    @Override
    public void viewShow() {
        System.out.println("\nResults:");

        for (NumberData d : list) {
            System.out.println(
                "Number: " + d.getNumber() +
                " | Octal: " + d.getOctal() +
                " (" + d.getOctalCount() + ")" +
                " | Hex: " + d.getHex() +
                " (" + d.getHexCount() + ")"
            );
        }

        System.out.println("End.");
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
 * Скорочене відображення (другий варіант)
 */
class ViewShort implements View {

    @Override
    public void viewShow() {
        System.out.println("\nShort view:");
        System.out.println("Data generated or loaded.");
    }

    @Override
    public void viewInit() {}

    @Override
    public void viewSave() {}

    @Override
    public void viewRestore() {}
}
