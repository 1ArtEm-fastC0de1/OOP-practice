import java.io.*;
import java.util.*;

/**
 * Singleton клас програми
 */
public class task5 {

    private static task5 instance = new task5();

    private task5() {}

    public static task5 getInstance() {
        return instance;
    }

    private List<NumberData> list = new ArrayList<>();
    private Stack<Command> history = new Stack<>();

    public static void main(String[] args) {
        getInstance().menu();
    }

    /**
     * Меню (Command)
     */
    public void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("a - add");
            System.out.println("s - show");
            System.out.println("d - delete last");
            System.out.println("u - undo");
            System.out.println("m - macro");
            System.out.println("q - exit");
            System.out.print("> ");

            char c = sc.next().charAt(0);

            Command cmd = null;

            switch (c) {
                case 'a' -> cmd = new AddCommand(list, history);
                case 'd' -> cmd = new RemoveCommand(list, history);
                case 's' -> cmd = new ShowCommand(list);
                case 'u' -> cmd = new UndoCommand(history);
                case 'm' -> cmd = new MacroCommand(list, history);
                case 'q' -> { return; }
            }

            if (cmd != null) cmd.execute();
        }
    }
}

////////////////////////////////////////////////////////////
// DATA
////////////////////////////////////////////////////////////

class NumberData implements Serializable {
    private String number;
    private int count1;
    private int count8;

    public NumberData(String number) {
        this.number = number;
        calculate();
    }

    private void calculate() {
        for (char c : number.toCharArray()) {
            if (c == '1') count1++;
            if (c == '8') count8++;
        }
    }

    @Override
    public String toString() {
        return number + " | 1=" + count1 + " | 8=" + count8;
    }
}

////////////////////////////////////////////////////////////
// COMMAND
////////////////////////////////////////////////////////////

interface Command {
    void execute();
    void undo();
}

////////////////////////////////////////////////////////////
// COMMANDS
////////////////////////////////////////////////////////////

class AddCommand implements Command {

    private List<NumberData> list;
    private Stack<Command> history;
    private NumberData last;

    public AddCommand(List<NumberData> list, Stack<Command> history) {
        this.list = list;
        this.history = history;
    }

    public void execute() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number: ");
        last = new NumberData(sc.next());
        list.add(last);
        history.push(this);
    }

    public void undo() {
        list.remove(last);
    }
}

////////////////////////////////////////////////////////////

class RemoveCommand implements Command {

    private List<NumberData> list;
    private Stack<Command> history;
    private NumberData removed;

    public RemoveCommand(List<NumberData> list, Stack<Command> history) {
        this.list = list;
        this.history = history;
    }

    public void execute() {
        if (!list.isEmpty()) {
            removed = list.remove(list.size() - 1);
            history.push(this);
        }
    }

    public void undo() {
        list.add(removed);
    }
}

////////////////////////////////////////////////////////////

class ShowCommand implements Command {

    private List<NumberData> list;

    public ShowCommand(List<NumberData> list) {
        this.list = list;
    }

    public void execute() {
        System.out.println("\n--- TABLE ---");
        for (NumberData d : list)
            System.out.println(d);
    }

    public void undo() {}
}

////////////////////////////////////////////////////////////
// UNDO
////////////////////////////////////////////////////////////

class UndoCommand implements Command {

    private Stack<Command> history;

    public UndoCommand(Stack<Command> history) {
        this.history = history;
    }

    public void execute() {
        if (!history.isEmpty()) {
            history.pop().undo();
            System.out.println("Undo OK");
        }
    }

    public void undo() {}
}

////////////////////////////////////////////////////////////
//  MACRO COMMAND
////////////////////////////////////////////////////////////

class MacroCommand implements Command {

    private List<NumberData> list;
    private Stack<Command> history;

    public MacroCommand(List<NumberData> list, Stack<Command> history) {
        this.list = list;
        this.history = history;
    }

    public void execute() {
        System.out.println("Macro: adding 2 numbers");

        Command c1 = new AddCommand(list, history);
        Command c2 = new AddCommand(list, history);

        c1.execute();
        c2.execute();
    }

    public void undo() {}
}