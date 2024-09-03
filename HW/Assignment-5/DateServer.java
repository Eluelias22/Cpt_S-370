// Elias Werede 
// Assignment 5 
// Part 1
// Date Server 

import java.net.*; 
import java.io.*; 


public class DateServer extends Thread{
    public DateServer() {

    }
    public void run(){
        try{
			// Creates a server socket that automatically binds to any available port.
			ServerSocket sock = new ServerSocket(0); 
            SysLib.cout("Listening on port " + sock.getLocalPort() + "\n");

			// Server enters an infinite loop listening for client connections.
			while(true){
				// Accept a connection from a client
				Socket client = sock.accept(); 
				PrintWriter pout = new PrintWriter(client.getOutputStream(), true); 
				
				// Sends the current date and time to the client. 
				pout.println(new java.util.Date().toString()); 

				// Closes the client socket after sending the data then returns to listening for more connections. 
				client.close(); 
				}
			}
		catch (IOException ioe) {
			// Prints any IO exceptions to standard error. 
			System.err.println(ioe + "\n");
            SysLib.exit();  
		}
    }
}
