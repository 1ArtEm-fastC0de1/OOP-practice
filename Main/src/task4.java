import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Повний самодостатній приклад:
 * - Item2d
 * - View / Viewable
 * - ViewResult / ViewableResult
 * - ViewTable / ViewableTable
 * - Main клас із меню
 * 
 * Демонструє: фабричний метод, поліморфізм, перевантаження/перевизначення, форматований вивід, серіалізацію
 * @author xone
 * @version 3.0
 */
public class task4 {

    // ===================== Item2d =====================
    public static class Item2d implements Serializable {
        private static final long serialVersionUID = 1L;
        private double x;
        private double y;

        public Item2d() { this(0.0,0.0); }
        public Item2d(double x, double y) { this.x = x; this.y = y; }

        public void setXY(double x, double y) { this.x = x; this.y = y; }
        public double getX() { return x; }
        public double getY() { return y; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Item2d)) return false;
            Item2d other = (Item2d) obj;
            return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
        }

        @Override
        public String toString() { return String.format("(%.0f; %.3f)", x, y); }
    }

    // ===================== Інтерфейси =====================
    public interface View {
        void viewHeader();
        void viewBody();
        void viewFooter();
        void viewShow();
        void viewInit();
        void viewSave() throws IOException;
        void viewRestore() throws Exception;
    }

    public interface Viewable {
        View getView();
    }

    // ===================== ViewResult =====================
    public static class ViewResult implements View {
        private static final String FNAME = "items.ser";
        private static final int DEFAULT_NUM = 10;
        private ArrayList<Item2d> items = new ArrayList<>();

        public ViewResult() { this(DEFAULT_NUM); }
        public ViewResult(int n) { for(int i=0;i<n;i++) items.add(new Item2d()); }

        public ArrayList<Item2d> getItems() { return items; }
        protected double calc(double x) { return Math.sin(Math.toRadians(x)); }

        public void init(double stepX) {
            double x = 0;
            for(Item2d item: items) { item.setXY(x, calc(x)); x += stepX; }
        }

        @Override
        public void viewInit() { init(Math.random()*360.0); }

        @Override
        public void viewSave() throws IOException {
            try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FNAME))) {
                os.writeObject(items);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void viewRestore() throws Exception {
            try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(FNAME))) {
                items = (ArrayList<Item2d>) is.readObject();
            }
        }

        @Override
        public void viewHeader() { System.out.println("Результати:"); }

        @Override
        public void viewBody() {
            for(Item2d item: items) System.out.print(item + " ");
            System.out.println();
        }

        @Override
        public void viewFooter() { System.out.println("Кінець."); }

        @Override
        public void viewShow() { viewHeader(); viewBody(); viewFooter(); }
    }

    // ===================== Фабрика для ViewResult =====================
    public static class ViewableResult implements Viewable {
        @Override
        public View getView() { return new ViewResult(); }
    }

    // ===================== ViewTable =====================
    public static class ViewTable extends ViewResult {
        private static final int DEFAULT_WIDTH = 20;
        private int width;

        public ViewTable() { width = DEFAULT_WIDTH; }
        public ViewTable(int width) { this.width = width; }
        public ViewTable(int width, int n) { super(n); this.width = width; }

        public int setWidth(int width) { return this.width = width; }
        public int getWidth() { return width; }

        private void outLine() { for(int i=0;i<width;i++) System.out.print('-'); }
        private void outLineLn() { outLine(); System.out.println(); }

        private void outHeader() {
            Formatter fmt = new Formatter();
            fmt.format("%" + ((width-3)/2) + "s | %" + ((width-3)/2) + "s\n","x","y");
            System.out.printf(fmt.toString());
        }

        private void outBody() {
            Formatter fmt = new Formatter();
            for(Item2d item: getItems())
                System.out.printf("%" + ((width-3)/2) + ".0f | %" + ((width-3)/2) + ".3f\n",item.getX(), item.getY());
        }

        // Перевантаження
        public final void init(int width) { this.width=width; viewInit(); }
        public final void init(int width, double stepX) { this.width=width; init(stepX); }

        // Перевизначення
        @Override
        public void init(double stepX) { System.out.print("Ініціалізація... "); super.init(stepX); System.out.println("готово."); }

        @Override
        public void viewHeader() { outHeader(); outLineLn(); }
        @Override
        public void viewBody() { outBody(); }
        @Override
        public void viewFooter() { outLineLn(); }
    }

    public static class ViewableTable extends ViewableResult {
        @Override
        public View getView() { return new ViewTable(); }
    }

    // ===================== Main =====================
    public static class Main {
        protected View view;

        public Main(View view) { this.view = view; }

        protected void menu() {
            String s = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            do {
                do {
                    System.out.print("Команда ('q' - вихід, 'v' - перегляд, 'g' - генерація, 's' - зберегти, 'r' - відновити): ");
                    try { s = in.readLine(); } catch(IOException e){System.out.println("Помилка: "+e); System.exit(0);}
                } while(s.length() != 1);

                switch(s.charAt(0)) {
                    case 'q': System.out.println("Вихід"); break;
                    case 'v': view.viewShow(); break;
                    case 'g': view.viewInit(); view.viewShow(); break;
                    case 's': try{view.viewSave();}catch(IOException e){System.out.println("Помилка серіалізації: "+e);} view.viewShow(); break;
                    case 'r': try{view.viewRestore();}catch(Exception e){System.out.println("Помилка відновлення: "+e);} view.viewShow(); break;
                    default: System.out.println("Невідома команда");
                }
            } while(s.charAt(0) != 'q');
        }

        public static void main(String[] args) {
            // Для перегляду як таблиці використати:
            Main main = new Main(new ViewableTable().getView());
            main.menu();
        }
    }
}
