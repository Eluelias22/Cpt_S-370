// Elias Werede
// Cpt_S 370
// Program 2 ThreadOS Shell 

import java.util.ArrayList;
import java.util.Scanner;

public class Shell extends Thread {
    // initialize command count to 1
    private int commandCount = 1; 

    //default constructor 
    public Shell() {
    }

    // constructor
    public Shell(String args[]){
    }
    public void run(){
        // scanner to read input from console
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // print shell prompt
            System.out.print("Shell[" + commandCount + "]% "); 

            // read input from user and remove spaces
            String input = scanner.nextLine().trim();

            // if user wants to exit
            if (input.equals("exit")){
                // exit the loop 
                break;
            }
            // if input is not empty
            if (!input.isEmpty()){
                // Increment command counter
                commandCount++;

                //split input into multiple commands
                String[] commands = input.split(";");

                // iterate through each command 
                for (String command : commands){
                    // split each command into subcommands
                    String[] subcommands = command.split("&"); 
                    ArrayList<Integer> childThreadIds = new ArrayList<>();
                    // iterate subcommands
                    for (String subcommand : subcommands){
                        
                        subcommand = subcommand.trim();
                        // to check subcommand is not empty
                        if (!subcommand.isEmpty()){
                            String[] args = SysLib.stringToArgs(subcommand); 
                            
                            int childThreadId = SysLib.exec(args); 
                            if (childThreadId != -1){
                                childThreadIds.add(childThreadId);
                            }
                        }
                    }
                    // Wait for all concurrent subcommands to complete 
                    for (Integer childThreadId : childThreadIds){
                        int completedThreadId;
                        do{
                            //wait for any thread to finish
                            completedThreadId = SysLib.join();
                        // repeat until finishes thread
                        }while (!childThreadIds.contains(completedThreadId));
                    }
                }
            }
        }
        //cleanup and exit
        SysLib.exit(); 
    }
}