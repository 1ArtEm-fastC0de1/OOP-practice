package task7;

import javax.swing.*;
import java.awt.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/* =======================
   ANNOTATIONS
   ======================= */

/**
 * Анотація для позначення інформації про поле.
 * Доступна під час виконання (Reflection).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Info {
    String value();
}

/**
 * Анотація, що існує лише на етапі компіляції.
 */
@Retention(RetentionPolicy.SOURCE)
@interface SourceOnly {
}

/**
 * Анотація, що зберігається у .class файлі,
 * але недоступна під час виконання.
 */
@Retention(RetentionPolicy.CLASS)
@interface ClassOnly {
}

/*
 * =======================
 * OBSERVER
 * =======================
 */

/**
 * Інтерфейс спостерігача (Observer).
 */
interface Observer {
    /**
     * Оновлення даних.
     * 
     * @param data список чисел
     */
    void update(List<Integer> data);
}

/**
 * Інтерфейс об'єкта, за яким спостерігають.
 */
interface Observable {
    void addObserver(Observer o);

    void notifyObservers();
}

/*
 * =======================
 * DATA
 * =======================
 */

/**
 * Клас даних.
 */
class NumberData {

    /** Значення числа */
    @Info("Збережене ціле число")
    int number;

    /** Конструктор */
    public NumberData(int n) {
        number = n;
    }
}

/*
 * =======================
 * MODEL (Observable)
 * =======================
 */

/**
 * Клас моделі, що зберігає колекцію чисел
 * та повідомляє спостерігачів про зміни.
 */
class TableView implements Observable {
    private ArrayList<NumberData> list = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private int count = 5;
    /** Встановити кількість елементів */
    public void setCount(int c) {
        count = c;
        notifyObservers();
    }
    /** Генерація випадкових чисел */
    public void viewInit() {
        list.clear();
        for (int i = 0; i < count; i++) {
            list.add(new NumberData((int) (Math.random() * 100)));
        }
        notifyObservers();
    }
    /** Отримати список чисел */
    public List<Integer> getNumbers() {
        return list.stream().map(d -> d.number).toList();
    }
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }
    @Override
    public void notifyObservers() {
        List<Integer> data = getNumbers();
        for (Observer o : observers) {
            o.update(data);
        }
    }
}

/*
 * =======================
 * OBSERVERS
 * =======================
 */

/** Вивід у консоль */
class ConsoleObserver implements Observer {
    @Override
    public void update(List<Integer> data) {
        System.out.println("Console: " + data);
    }
}

/** Сортування та вивід у консоль */
class SortObserver implements Observer {
    @Override
    public void update(List<Integer> data) {
        List<Integer> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        System.out.println("Sorted: " + sorted);
    }
}

/*
 * =======================
 * GRAPH
 * =======================
 */

/** Графічний спостерігач*/
class GraphObserver extends JPanel implements Observer {
    private List<Integer> data = new ArrayList<>();
    @Override
    public void update(List<Integer> data) {
        this.data = data;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data.isEmpty())
            return;
        int width = getWidth();
        int height = getHeight();
        int step = width / data.size();
        for (int i = 0; i < data.size(); i++) {
            int value = data.get(i);
            int x = i * step;
            int y = height - value;

            // Синя смужка
            g.setColor(Color.BLUE);
            g.fillRect(x, y, step - 2, value);

            // Підпис числа над смужкою
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(value), x + (step - 2) / 4, y - 5);
        }
    }
}

/*
 * =======================
 * STATS OBSERVER
 * =======================
 */

/** Спостерігач для min/max/avg */
class StatsObserver implements Observer {

    private JLabel minLabel, maxLabel, avgLabel;

    /** Конструктор */
    public StatsObserver(JLabel min, JLabel max, JLabel avg) {
        this.minLabel = min;
        this.maxLabel = max;
        this.avgLabel = avg;
    }

    @Override
    public void update(List<Integer> data) {

        if (data.isEmpty()) {
            minLabel.setText("Min: -");
            maxLabel.setText("Max: -");
            avgLabel.setText("Avg: -");
            return;
        }

        int min = data.stream().min(Integer::compare).orElse(0);
        int max = data.stream().max(Integer::compare).orElse(0);
        double avg = data.stream().mapToInt(i -> i).average().orElse(0);

        minLabel.setText("Min: " + min);
        maxLabel.setText("Max: " + max);
        avgLabel.setText("Avg: " + avg);
    }
}

/*
 * =======================
 * REFLECTION
 * =======================
 */

/** Демонстрація reflection */
class ReflectionDemo {

    /** Вивід інформації про анотації */
    public static void show() {
        try {
            Class<?> cls = NumberData.class;

            for (Field f : cls.getDeclaredFields()) {
                if (f.isAnnotationPresent(Info.class)) {
                    Info info = f.getAnnotation(Info.class);

                    System.out.println("Field: " + f.getName());
                    System.out.println("Annotation: " + info.value());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * =======================
 * GUI
 * =======================
 */

/** Графічний інтерфейс */
class GUI {

    /** Запуск GUI */
    public static void start(TableView view) {

        JFrame frame = new JFrame("Observer App");

        GraphObserver graph = new GraphObserver();
        view.addObserver(graph);

        JPanel controls = new JPanel();

        JTextField countField = new JTextField("5", 5);
        JButton setBtn = new JButton("Set Count");
        JButton genBtn = new JButton("Generate");

        JLabel minLabel = new JLabel("Min: -");
        JLabel maxLabel = new JLabel("Max: -");
        JLabel avgLabel = new JLabel("Avg: -");

        JPanel results = new JPanel();
        results.add(minLabel);
        results.add(maxLabel);
        results.add(avgLabel);

        view.addObserver(new StatsObserver(minLabel, maxLabel, avgLabel));

        setBtn.addActionListener(e -> {
            try {
                int c = Integer.parseInt(countField.getText());
                view.setCount(c);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Некоректне число");
            }
        });

        genBtn.addActionListener(e -> view.viewInit());

        controls.add(new JLabel("Count:"));
        controls.add(countField);
        controls.add(setBtn);
        controls.add(genBtn);

        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(graph, BorderLayout.CENTER);
        frame.add(results, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

/*
 * =======================
 * MAIN
 * =======================
 */

/** Головний клас програми */
public class task7 {

    /** Точка входу */
    public static void main(String[] args) {

        TableView view = new TableView();

        view.addObserver(new ConsoleObserver());
        view.addObserver(new SortObserver());

        ReflectionDemo.show();

        GUI.start(view);
    }
}