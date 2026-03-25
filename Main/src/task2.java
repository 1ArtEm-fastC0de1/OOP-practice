import java.io.*;

/**
 * Головний клас для запуску програми
 */
public class task2 {

    public static void main(String[] args) {

        // Тест обчислення
        NumberData data = new NumberData("11888821");

        Calculator calc = new Calculator(data);
        calc.calculate();

        assert data.getCount1() == 3 : "Помилка підрахунку 1";
        assert data.getCount8() == 4 : "Помилка підрахунку 8";

        System.out.println("Calculation test passed!");

        // Тест серіалізації
        try {
            SerializationDemo.save(data, "test.ser");
            NumberData loaded = SerializationDemo.load("test.ser");

            assert loaded.getCount1() == data.getCount1();
            assert loaded.getCount8() == data.getCount8();

            System.out.println("Serialization test passed!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Демонстрація
        SerializationDemo.runDemo();
    }
}

/**
 * Клас для зберігання параметрів і результатів обчислення.
 */
class NumberData implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Вхідне число */
    private String number;

    /** Кількість цифр '1' */
    private int count1;

    /** Кількість цифр '8' */
    private int count8;

    /** transient поле (не серіалізується) */
    private transient String tempInfo;

    /**
     * Конструктор
     * @param number десяткове число
     */
    public NumberData(String number) {
        this.number = number;
        this.tempInfo = "Temporary info";
    }

    public String getNumber() { return number; }
    public int getCount1() { return count1; }
    public int getCount8() { return count8; }

    public void setCount1(int count1) { this.count1 = count1; }
    public void setCount8(int count8) { this.count8 = count8; }

    public String getTempInfo() { return tempInfo; }
    public void setTempInfo(String tempInfo) { this.tempInfo = tempInfo; }
}

/**
 * Клас для виконання обчислень (агрегування)
 */
class Calculator {

    private NumberData data;

    public Calculator(NumberData data) {
        this.data = data;
    }

    /**
     * Підрахунок цифр '1' та '8'
     */
    public void calculate() {
        int count1 = 0;
        int count8 = 0;

        for (char c : data.getNumber().toCharArray()) {
            if (c == '1') count1++;
            if (c == '8') count8++;
        }

        data.setCount1(count1);
        data.setCount8(count8);
    }
}

/**
 * Клас для демонстрації серіалізації
 */
class SerializationDemo {

    /**
     * Збереження об'єкта у файл
     */
    public static void save(NumberData data, String filename) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(data);
        oos.close();
    }

    /**
     * Завантаження об'єкта з файлу
     */
    public static NumberData load(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        NumberData data = (NumberData) ois.readObject();
        ois.close();
        return data;
    }

    /**
     * Демонстрація роботи серіалізації
     */
    public static void runDemo() {
        try {
            NumberData data = new NumberData("18123881");

            Calculator calc = new Calculator(data);
            calc.calculate();

            System.out.println("\n--- Before serialization ---");
            System.out.println("Count 1: " + data.getCount1());
            System.out.println("Count 8: " + data.getCount8());
            System.out.println("Temp: " + data.getTempInfo());

            save(data, "data.ser");

            NumberData loaded = load("data.ser");

            System.out.println("\n--- After deserialization ---");
            System.out.println("Count 1: " + loaded.getCount1());
            System.out.println("Count 8: " + loaded.getCount8());
            System.out.println("Temp: " + loaded.getTempInfo());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}