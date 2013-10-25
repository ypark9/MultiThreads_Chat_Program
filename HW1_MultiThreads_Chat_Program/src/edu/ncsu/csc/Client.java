package edu.ncsu.csc;
/**
 * reuse code from http://alecture.blogspot.com/2011/06/socket-programming.html 
 * and http://www.ase.md/~aursu/ClientServerThreads.html
 */


import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Chat program Client part. 
 * 
 * @author YoonsooPark
 *
 */

public class Client implements Runnable {

  // The client socket
  private static Socket clientSocket = null;
  // The output stream
  private static PrintStream outStream = null;
  // The input stream
  private static DataInputStream inputStream = null;

  private static BufferedReader message = null;
  private static boolean closed = false;
  
/**
 * it calls when main method calls .start();
 */
 public void run() {
//process socket until finds"bye" message
   String response;
   try {
     while ((response = inputStream.readLine()) != null) {
       System.out.println(response);
       if (response.indexOf("bye") != -1)
         break;
     }
     closed = true;
   } catch (IOException e) {
     System.err.println("IOException:  " + e);
   }
 }
  
  
  public static void main(String[] args) {

	//add new Object  
	MyObject o;
    // The default port.
    int portNumber = 8080;
  
    String host = "localhost";

    if (args.length < 2) {
      System.out
          .println("ClientSide information <host> <portNumber>\n"
              + "host=" + host + ", portNumber=" + portNumber);
    } else {
    	//getting name of chatter
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }

 // open socket for Input and Output
    try {
      clientSocket = new Socket(host, portNumber);
      message = new BufferedReader(new InputStreamReader(System.in));
      outStream = new PrintStream(clientSocket.getOutputStream());
      inputStream = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("No host INFO about " + host);
    } catch (IOException e) {
      System.err.println("Impossible to get Input & outputStream from "
          + host);
    }

    /*
     * Since we initialized every parts, we need to write some data to socket.
     * 
     */
    if (clientSocket != null && outStream != null && inputStream != null) {
      try {

        //
    	//Create a thread which can read from server....
        new Thread(new Client()).start();
        while (!closed) {
        	outStream.println(message.readLine().trim());
        }
        /*
         * Close the outputStream, close the inputStream, 
         * and close the socket.
         */
        outStream.close();
        inputStream.close();
        clientSocket.close();
        
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }


}