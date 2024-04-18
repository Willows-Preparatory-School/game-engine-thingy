package network_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            System.out.println("Server response: " + in.readLine());
            if (userInput.equals("exit")) {
                System.out.println("Received exit command, exiting...");
                break;
            }
        }

        in.close();
        out.close();
        stdIn.close();
        socket.close();
    }
}
