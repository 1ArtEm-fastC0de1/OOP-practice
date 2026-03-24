public class App {
    public static void main(String[] args) throws Exception{
        args = new String[]{"1", "2", "3"};
        
        System.out.println("Count args: " + args.length);

        for(int i = 0; i < args.length; i++){
            System.out.println("args[" + i + "] = " + args[i]);
        }
    }
}
