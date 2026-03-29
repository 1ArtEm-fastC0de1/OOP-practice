import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Приклад: обробка колекцій з використанням шаблонів Singleton, Command та макрокоманд
 * Демонструє:
 * - Singleton: Application
 * - Command: ChangeItemCommand, GenerateConsoleCommand, Save/Restore/View
 * - Макрокоманда: Menu
 * - Undo операції
 * @author xone
 * @version 1.0
 */
public class task5 {

    // ===================== Item2d =====================
    public static class Item2d implements Serializable {
        private static final long serialVersionUID = 1L;
        private double x, y;

        public Item2d() { this(0,0); }
        public Item2d(double x, double y) { this.x=x; this.y=y; }

        public void setXY(double x, double y) { this.x=x; this.y=y; }
        public double getX() { return x; }
        public double getY() { return y; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Item2d)) return false;
            Item2d o = (Item2d)obj;
            return Double.compare(x, o.x)==0 && Double.compare(y, o.y)==0;
        }

        @Override
        public String toString() { return String.format("(%.0f; %.3f)", x, y); }
    }

    // ===================== Інтерфейси Command =====================
    public interface Command { void execute(); }
    public interface ConsoleCommand extends Command { char getKey(); }

    // ===================== ChangeItemCommand =====================
    public static class ChangeItemCommand implements Command {
        private Item2d item;
        private double offset;
        private double prevY; // Для undo

        public Item2d setItem(Item2d item) { return this.item = item; }
        public Item2d getItem() { return item; }

        public double setOffset(double offset) { return this.offset = offset; }
        public double getOffset() { return offset; }

        @Override
        public void execute() {
            if(item!=null){
                prevY = item.getY();
                item.setY(item.getY() * offset);
            }
        }

        public void undo() {
            if(item!=null) item.setY(prevY);
        }
    }

    // ===================== View =====================
    public interface View {
        void viewInit();
        void viewShow();
        void viewSave() throws IOException;
        void viewRestore() throws Exception;
        ArrayList<Item2d> getItems();
    }

    // ===================== ViewResult =====================
    public static class ViewResult implements View {
        private static final String FNAME="items.ser";
        protected ArrayList<Item2d> items = new ArrayList<>();

        public ViewResult() { this(10); }
        public ViewResult(int n) { for(int i=0;i<n;i++) items.add(new Item2d()); }

        @Override public ArrayList<Item2d> getItems() { return items; }

        protected double calc(double x){ return Math.sin(Math.toRadians(x)); }

        @Override
        public void viewInit() {
            double step = Math.random()*10+1;
            double x=0;
            for(Item2d item: items){ item.setXY(x, calc(x)); x+=step; }
        }

        @Override
        public void viewShow() {
            System.out.println("Колекція Item2d:");
            for(Item2d i: items) System.out.println(i);
            System.out.println("Кінець колекції.\n");
        }

        @Override
        public void viewSave() throws IOException {
            try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FNAME))){
                os.writeObject(items);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void viewRestore() throws Exception {
            try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(FNAME))){
                items = (ArrayList<Item2d>)is.readObject();
            }
        }
    }

    // ===================== ViewConsoleCommand =====================
    public static class ViewConsoleCommand implements ConsoleCommand {
        private View view;
        public ViewConsoleCommand(View view) { this.view=view; }
        @Override public char getKey(){ return 'v'; }
        @Override public void execute(){ System.out.println("View collection"); view.viewShow(); }
        @Override public String toString(){ return "'v'iew"; }
    }

    // ===================== GenerateConsoleCommand =====================
    public static class GenerateConsoleCommand implements ConsoleCommand {
        private View view;
        public GenerateConsoleCommand(View view) { this.view=view; }
        @Override public char getKey(){ return 'g'; }
        @Override public void execute(){ System.out.println("Generate collection"); view.viewInit(); view.viewShow(); }
        @Override public String toString(){ return "'g'enerate"; }
    }

    // ===================== ChangeConsoleCommand =====================
    public static class ChangeConsoleCommand extends ChangeItemCommand implements ConsoleCommand {
        private View view;
        public ChangeConsoleCommand(View view){ this.view=view; }
        @Override public char getKey(){ return 'c'; }
        @Override public String toString(){ return "'c'hange"; }
        @Override public void execute(){
            System.out.println("Change items");
            setOffset(Math.random()*100.0);
            for(Item2d item: view.getItems()){ setItem(item); super.execute(); }
            view.viewShow();
        }
    }

    // ===================== SaveConsoleCommand =====================
    public static class SaveConsoleCommand implements ConsoleCommand {
        private View view;
        public SaveConsoleCommand(View view){ this.view=view; }
        @Override public char getKey(){ return 's'; }
        @Override public String toString(){ return "'s'ave"; }
        @Override public void execute(){
            System.out.println("Save collection");
            try{ view.viewSave(); } catch(IOException e){ System.err.println(e); }
        }
    }

    // ===================== RestoreConsoleCommand =====================
    public static class RestoreConsoleCommand implements ConsoleCommand {
        private View view;
        public RestoreConsoleCommand(View view){ this.view=view; }
        @Override public char getKey(){ return 'r'; }
        @Override public String toString(){ return "'r'estore"; }
        @Override public void execute(){
            System.out.println("Restore collection");
            try{ view.viewRestore(); } catch(Exception e){ System.err.println(e); }
            view.viewShow();
        }
    }

    // ===================== Menu (макрокоманда) =====================
    public static class Menu implements Command {
        private List<ConsoleCommand> menu = new ArrayList<>();
        private Stack<ChangeItemCommand> undoStack = new Stack<>();

        public ConsoleCommand add(ConsoleCommand c){ menu.add(c); return c; }

        @Override
        public void execute(){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String s;
            menu: while(true){
                do{
                    System.out.print(this);
                    try{ s=in.readLine(); } catch(IOException e){ System.err.println(e); return; }
                }while(s.length()!=1);
                char key = s.charAt(0);
                if(key=='q'){ System.out.println("Exit"); break menu; }

                boolean found=false;
                for(ConsoleCommand c: menu){
                    if(c.getKey()==key){
                        if(c instanceof ChangeConsoleCommand)
                            undoStack.push((ChangeConsoleCommand)c);
                        c.execute();
                        found=true; break;
                    }
                }
                if(!found) System.out.println("Wrong command.");
            }
        }

        @Override
        public String toString(){
            StringBuilder sb=new StringBuilder("Enter command:\n");
            for(ConsoleCommand c: menu) sb.append(c+", ");
            sb.append("'q'uit: ");
            return sb.toString();
        }

        public void undo(){
            if(!undoStack.isEmpty()){
                ChangeItemCommand cmd = undoStack.pop();
                cmd.undo();
            }
        }
    }

    // ===================== Application (Singleton) =====================
    public static class Application {
        private static Application instance = new Application();
        private View view = new ViewResult();
        private Menu menu = new Menu();
        private Application(){ }
        public static Application getInstance(){ return instance; }
        public void run(){
            menu.add(new ViewConsoleCommand(view));
            menu.add(new GenerateConsoleCommand(view));
            menu.add(new ChangeConsoleCommand(view));
            menu.add(new SaveConsoleCommand(view));
            menu.add(new RestoreConsoleCommand(view));
            menu.execute();
        }
    }

    // ===================== Main =====================
    public static class Main {
        public static void main(String[] args){
            Application app = Application.getInstance();
            app.run();
        }
    }
}
