package network_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    // Config:
    static int PORT = 5000; // What port do we listen on?

    // Misc
    static boolean isRunning = false;

    public static void main(String args[]) throws IOException
    {
        System.out.println("Hello from server!");
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Started server on port: " + serverSocket.getLocalPort());

        isRunning = true;
        while (isRunning)
        {
            System.out.println("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept(); // Client connection
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                out.println("Server received: " + inputLine);
                if (inputLine.equals("exit")) {
                    System.out.println("Received exit command, exiting...");
                    isRunning = false;
                    break;
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        }
    }
}
