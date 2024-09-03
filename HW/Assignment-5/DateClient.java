// Elias Werede 
// Assignment 5 
// Part 1
// Date Client 

import java.net.*; 
import java.io.*; 


public class DateClient extends Thread{
    private final String[] args; 

    public DateClient(){
        this.args = new String[0];
    }
    public DateClient(String[] args){
        this.args = args; 
    }
    public void run(){
        try {
            if (args.length == 0){
                SysLib.cerr("Port number parameter needed(5000 - 5500) + \n"); 
                SysLib.exit(); 
            }else {
                // Parses the port number from the first argument.
                int portNumber = Integer.parseInt(args[0]); 

                /*Make connection to server socket*/ 
                Socket sock = new Socket("localhost",portNumber); // Establishes a socket connection to the server on localhost. 
                
                // Creates an InputStream to read data sent by the server.
                InputStream in = sock.getInputStream(); 
                BufferedReader bin = new BufferedReader(new InputStreamReader(in)); 

                // Reads data from the server line by line until there is nothing left to read. 
                String line; 
                while ((line = bin.readLine()) != null)
                    SysLib.cout(line + "\n"); 


                /* close the socket connection*/ 
                sock.close();
                SysLib.exit();  
            }
        }catch (IOException ioe){
            // Prints an error message to standard error if an IO exception occurs. 
            System.err.println(ioe + "\n"); 
            SysLib.exit();  
        }
    }
}


