package network_test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    // Config:
    static int PORT = 5000; // What port do we listen on?

    // Misc
    boolean isRunning = false;
    private ServerSocket serverSocket;

    // Funcs
    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException
    {
        isRunning = true;
        System.out.println("Reached server start() func...");
        System.out.println("Waiting for clients...");
        while (isRunning)
        {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }

    public static void main(String args[]) throws IOException
    {
        System.out.println("Hello from server!");
        try
        {
            System.out.println("Trying to run server on port: " + PORT);
            Server server = new Server(PORT);
            server.start();
        }
        catch (IOException e)
        {
            System.out.println("Uh oh, something went wrong...");
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread
    {
        boolean isRunning = false;

        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {
            isRunning = true;
            System.out.println("Received client connection...");
            System.out.println("Client Address: " + clientSocket.getInetAddress());
            System.out.println("Client Port: " + clientSocket.getPort());
            System.out.println("Client Channel: " + clientSocket.getChannel());
            System.out.println("Client local address: " + clientSocket.getLocalAddress());
            System.out.println("Client local port: " + clientSocket.getLocalPort());

            try
            {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());

                out.writeUTF("What is your name?");
                String userName = in.readUTF();
                out.writeUTF("Hello " + userName);

                while (isRunning)
                {
                    String clientMessage = in.readUTF();
                    out.writeUTF(clientMessage);
                    if(clientMessage.equals("exit"))
                    {
                        out.writeUTF("got exit command, goodbye.");
                        // got exit command, exit
                        isRunning = false;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    in.close();
                    out.close();
                    clientSocket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}


