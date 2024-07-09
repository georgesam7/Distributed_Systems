import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// This class is responsible for listening for new clients and creating a new ClientHandler for each one
public class ClientServer extends Thread{
    private ServerSocket clientServerSocket;
    private boolean running = true;

    // This constructor initializes the ServerSocket
    public ClientServer(int clientPort){
        try {
            clientServerSocket = new ServerSocket(clientPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called when the thread is started
    // It listens for new clients and creates a new ClientHandler for each one
    @Override
    public synchronized void run()
    {
        while (running){

            try {
                Socket clientSocket = clientServerSocket.accept();
                System.out.println("New Client connected from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // This method is called when the thread is stopped
    public synchronized void stopThread() {
        running = false;
    }


}
