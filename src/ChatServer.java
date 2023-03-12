import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatServer {
    ArrayList clientOutputStream;

    /**
     * This inner class is used to handle the incoming client messages
     * */
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        /**
         * @param clientSocket The client that has made a connection with the server
         * @author <a href="https://github.com/SergioMir018">...</a>
         * */
        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Belongs to the thread 't' that it's in charged of constantly reading the messages from the clients and
         * delivering them to all the other clients connected to the chat
         * @author <a href="https://github.com/SergioMir018">...</a>
         * */
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    tellEverybody(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Used by the method run() and it's for sending the incoming messages form a client and distributing them to all
     * the other users of the chat
     *
     * @param message the incoming message from the client
     * @author <a href="https://github.com/SergioMir018">...</a>
     * */
    private void tellEverybody(String message) {
        Iterator it = clientOutputStream.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Stars the whole process of this server and constantly checks for new messages and new client connections
     * @author <a href="https://github.com/SergioMir018">...</a>
     * */
    private void go() throws IOException {
        clientOutputStream = new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().go();
    }
}