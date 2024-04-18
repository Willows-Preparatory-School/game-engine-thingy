package network_test;

import java.io.*;
import java.net.*;

public class Client
{
    // Config
    static int PORT = 5000;
    static String HOST = "127.0.0.1";

    //Misc
    static boolean isRunning = false;

    public static void main(String args[]) throws IOException
    {
        System.out.println("Hello from client!");
        System.out.println("Connecting to server...");
        Socket socket = new Socket(HOST, PORT); // Connect to the server
        System.out.println("Connected to server!");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Wait for the server's message asking for the name
        String serverMessage = in.readUTF();
        System.out.println(serverMessage);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userName = stdIn.readLine();
        out.writeUTF(userName); // Send the name to the server

        // After sending the name, wait for the server's greeting to prevent a strange desync.
        String serverResponse = in.readUTF();
        System.out.println(serverResponse);

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.writeUTF(userInput);
            System.out.println("Server response: " + in.readUTF());
        }

        in.close();
        out.close();
        stdIn.close();
        socket.close();
    }
}
