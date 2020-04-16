import java.net.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    ArrayList<PrintWriter> inputStreams;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
//    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    public class ClientHandle implements Runnable {
        Socket socket;
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        Boolean isConnected = false;

        public ClientHandle(Socket clientSocket) {
            try {
                socket = clientSocket;
                inputStreamReader = new InputStreamReader((socket.getInputStream()));
                bufferedReader = new BufferedReader(inputStreamReader);
                isConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println(message);
                    sendToEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToEveryone(String message) {
        Iterator iterator = inputStreams.iterator();
        try {
            while (iterator.hasNext()) {
                PrintWriter printWriter = (PrintWriter) iterator.next();
                printWriter.println(message);
                printWriter.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        inputStreams = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            System.out.println("Server online. Server version (1.0.1).\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
                inputStreams.add(printWriter);
                threadPool.execute(new ClientHandle(clientSocket));
                System.out.println("Connected new Client has connected.\t" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] argv) {
        Server client = new Server();
        client.run();
    }
}
