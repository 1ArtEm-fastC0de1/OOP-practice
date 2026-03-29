import java.io.*;

/**
 * Головний клас
 */
public class task2 {

    public static void main(String[] args) {

        // Тест
        NumberData data = new NumberData(100);

        Calculator calc = new Calculator(data);
        calc.calculate();

        System.out.println("Octal: " + data.getOctal());
        System.out.println("Hex: " + data.getHex());

        System.out.println("Octal digits: " + data.getOctalCount());
        System.out.println("Hex digits: " + data.getHexCount());

        // Перевірка
        assert data.getOctalCount() == 3 : "Помилка 8-річного підрахунку";
        assert data.getHexCount() == 2 : "Помилка 16-річного підрахунку";

        System.out.println("Calculation test passed!");

        // Тест серіалізації
        try {
            SerializationDemo.save(data, "test.ser");
            NumberData loaded = SerializationDemo.load("test.ser");

            assert loaded.getOctalCount() == data.getOctalCount();
            assert loaded.getHexCount() == data.getHexCount();

            System.out.println("Serialization test passed!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Демонстрація
        SerializationDemo.runDemo();
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
        this.tempInfo = "Temporary info";
    }

    public int getNumber() { return number; }

    public String getOctal() { return octal; }
    public String getHex() { return hex; }

    public int getOctalCount() { return octalCount; }
    public int getHexCount() { return hexCount; }

    public void setOctal(String octal) { this.octal = octal; }
    public void setHex(String hex) { this.hex = hex; }

    public void setOctalCount(int count) { this.octalCount = count; }
    public void setHexCount(int count) { this.hexCount = count; }

    public String getTempInfo() { return tempInfo; }
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

        int number = data.getNumber();

        // Переведення
        String octal = Integer.toOctalString(number);
        String hex = Integer.toHexString(number);

        // Підрахунок
        data.setOctal(octal);
        data.setHex(hex);

        data.setOctalCount(octal.length());
        data.setHexCount(hex.length());
    }
}

/**
 * Серіалізація
 */
class SerializationDemo {

    public static void save(NumberData data, String filename) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(data);
        oos.close();
    }

    public static NumberData load(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        NumberData data = (NumberData) ois.readObject();
        ois.close();
        return data;
    }

    public static void runDemo() {
        try {
            NumberData data = new NumberData(255);

            Calculator calc = new Calculator(data);
            calc.calculate();

            System.out.println("\n--- Before serialization ---");
            System.out.println("Octal: " + data.getOctal());
            System.out.println("Hex: " + data.getHex());
            System.out.println("Temp: " + data.getTempInfo());

            save(data, "data.ser");

            NumberData loaded = load("data.ser");

            System.out.println("\n--- After deserialization ---");
            System.out.println("Octal: " + loaded.getOctal());
            System.out.println("Hex: " + loaded.getHex());
            System.out.println("Temp: " + loaded.getTempInfo());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
