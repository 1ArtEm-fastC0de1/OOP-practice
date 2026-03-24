package com.example.Main;

public class App {
    
    public static void main(String[] args) {
        
        args = new String[]{"1", "2", "3"};
        
        System.out.println("Кількість аргументів: " + args.length);

        for(int i = 0; i < args.length; i++){
            System.out.println("args[" + i + "] = " + args[i]);
        }
    }
}