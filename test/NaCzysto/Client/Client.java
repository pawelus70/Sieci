import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public class ClientData implements Serializable {
        String name;
        Socket socket;
        BufferedReader bufferedReader;
        PrintWriter printWriter;
        Boolean isConnected;
        InputStreamReader inputStreamReader;

        public void showStatus() {
            System.out.println("Is Connected: " + this.isConnected);
            System.out.println("Connected as: " + this.name);
        }
    }

    ClientData clientData = new ClientData();

    public void connectToServer(String ip, int port) {
        try {
            clientData.socket = new Socket(ip, port);
            clientData.isConnected = true;
            clientData.inputStreamReader = new InputStreamReader(clientData.socket.getInputStream());
            clientData.bufferedReader = new BufferedReader(clientData.inputStreamReader);
            clientData.printWriter = new PrintWriter(clientData.socket.getOutputStream());
        } catch (Exception e) {
            clientData.isConnected = false;
            e.printStackTrace();
            clientData.printWriter = null;
            clientData.bufferedReader = null;
            clientData.inputStreamReader = null;
        }
    }

    public void run() {
        try {
            System.out.println("Enter your name: ");
            clientData.name = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if (clientData.name.equals("")) clientData.name = "Anonymous";
            connectToServer("127.0.0.1", 4242);
            clientData.showStatus();
            threadPool.execute(new SendToEveryone());
            threadPool.execute(new WarningReceiver());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class SendToEveryone implements Runnable {
        String message;

        public void run() {
            while (clientData.isConnected) {
                try {
                    message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!(message.equals(""))) {
                    clientData.printWriter.println(clientData.name + ": " + message);
                    clientData.printWriter.flush();
                    message = "";
                }
            }
        }
    }

    public class WarningReceiver implements Runnable {
        String message;

        public void run() {
            try {
                while (clientData.bufferedReader != null) {
                    message = clientData.bufferedReader.readLine();
                    System.out.println(message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.run();
    }
}
