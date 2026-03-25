import java.io.*;
import java.util.*;

/**
 * Головний клас
 */
public class task3 {

    public static void main(String[] args) {

        // Колекція результатів
        List<NumberData> results = new ArrayList<>();

        // Додаємо дані
        results.add(process("11888821"));
        results.add(process("18111188"));

        // Фабрика
        DisplayFactory factory = new TextDisplayFactory();

        // Виведення
        System.out.println("=== RESULTS ===");
        for (NumberData data : results) {
            Display display = factory.createDisplay();
            display.show(data);
        }

        // Серіалізація колекції
        try {
            save(results, "collection.ser");
            List<NumberData> loaded = load("collection.ser");

            System.out.println("\n=== AFTER DESERIALIZATION ===");
            for (NumberData data : loaded) {
                Display display = factory.createDisplay();
                display.show(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Обробка числа
     */
    public static NumberData process(String number) {
        NumberData data = new NumberData(number);
        Calculator calc = new Calculator(data);
        calc.calculate();
        return data;
    }

    /**
     * Збереження колекції
     */
    public static void save(List<NumberData> list, String file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(list);
        oos.close();
    }

    /**
     * Завантаження колекції
     */
    public static List<NumberData> load(String file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        List<NumberData> list = (List<NumberData>) ois.readObject();
        ois.close();
        return list;
    }
}

/**
 * Клас даних (Serializable)
 */
class NumberData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String number;
    private int count1;
    private int count8;

    private transient String tempInfo;

    public NumberData(String number) {
        this.number = number;
        this.tempInfo = "Temp info";
    }

    public String getNumber() { return number; }
    public int getCount1() { return count1; }
    public int getCount8() { return count8; }

    public void setCount1(int c) { count1 = c; }
    public void setCount8(int c) { count8 = c; }
}

/**
 * Клас обчислення (агрегування)
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

/**
 * Інтерфейс відображення
 */
interface Display {
    void show(NumberData data);
}

/**
 * Конкретна реалізація (текстовий вивід)
 */
class TextDisplay implements Display {

    @Override
    public void show(NumberData data) {
        System.out.println("Number: " + data.getNumber() +
                " | Count1: " + data.getCount1() +
                " | Count8: " + data.getCount8());
    }
}

/**
 * Інтерфейс фабрики
 */
interface DisplayFactory {
    Display createDisplay();
}

/**
 * Конкретна фабрика
 */
class TextDisplayFactory implements DisplayFactory {

    @Override
    public Display createDisplay() {
        return new TextDisplay();
    }
}