import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Server {
    ArrayList<ClientHandle> clientHandles;
    int amountConnected = 0;
    // private final ExecutorService threadPool = Executors.newCachedThreadPool();
    // private final ExecutorService threadPool = Executors.newFixedThreadPool(200);
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    public void ShareClientList(int index) {
        clientHandles.get(index).printMessageWriter.println("001" + clientHandles.size());
        clientHandles.get(index).printMessageWriter.flush();
        for (int i = 0; i < clientHandles.size(); i++) {
            clientHandles.get(index).printMessageWriter.println(clientHandles.get(i).name);
            clientHandles.get(index).printMessageWriter.flush();
            clientHandles.get(index).printMessageWriter.println(clientHandles.get(i).index);
            clientHandles.get(index).printMessageWriter.flush();
        }
    }

    public class ClientHandle implements Runnable {
        String name;
        int index;
        Long time;
        Socket socketMessage;
        BufferedReader bufferedMessageReader;
        InputStreamReader inputMessageStreamReader;
        Boolean isConnected = false;
        PrintWriter printMessageWriter;


        public ClientHandle(Socket clientSocket, int index) {
            try {
                socketMessage = clientSocket;
                printMessageWriter = new PrintWriter(socketMessage.getOutputStream());
                inputMessageStreamReader = new InputStreamReader((socketMessage.getInputStream()));
                bufferedMessageReader = new BufferedReader(inputMessageStreamReader);
                isConnected = true;
                this.index = index;
                printMessageWriter.println(index);
                printMessageWriter.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = bufferedMessageReader.readLine()) != null) {
                    time = System.nanoTime();
                    System.out.println(name + ", " + LocalTime.now().withNano(0) + ", " + message);
                    requestListener(message, index);
                }
            } catch (IOException e) {
                this.isConnected = false;
                e.printStackTrace();
                onDisconnection(index);
            }
        }

    }


    public void requestListener(String message, int index) {
        if (message.startsWith("000")) { //Echo

            String TempTime = message.substring(3);
            clientHandles.get(index).printMessageWriter.println("000" + (System.nanoTime() - Long.parseLong(TempTime)));
            clientHandles.get(index).printMessageWriter.flush();


        } else if (message.startsWith("001")) {//give list of users
            ShareClientList(index);
        } else if (message.startsWith("002")) {//change name
            clientHandles.get(index).name = message.substring(3);
        } else if (message.startsWith("003")) {//send to everyone
            for (int i = 0; i < clientHandles.size(); i++) {
                if (i == index) continue;
                message = message.substring(3);
                clientHandles.get(i).printMessageWriter.println(clientHandles.get(index).name + ": " + message);
                clientHandles.get(i).printMessageWriter.flush();
            }
        } else {
            clientHandles.get(index).printMessageWriter.println("Unrecognizable request");
            clientHandles.get(index).printMessageWriter.flush();

        }
    }

    public void run() {
        clientHandles = new ArrayList();   //client list

        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            System.out.println("Server online. Server version (1.0.1).\n");

            while (true) {
                Socket clientSocket = serverSocket.accept(); //accept request

                //receive connection message
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                clientHandles.add(new ClientHandle(clientSocket, (clientHandles.size()))); //add client to database
                threadPool.execute(clientHandles.get(clientHandles.size() - 1)); //execute client thread
                clientHandles.get(clientHandles.size() - 1).name = bufferedReader.readLine(); //set client name

                System.out.println("Connected new Client has connected.\t" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress());

                amountConnected = clientHandles.size() - 1;
                showConnected();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisconnection(int index) {
        clientHandles.remove(index);
        for (int i = 0; i < clientHandles.size(); i++) {
            clientHandles.get(i).index = i;
        }
        amountConnected = clientHandles.size() - 1;
    }

    public void showConnected() {
        System.out.println("Currently connected: ");
        for (int i = 0; i < clientHandles.size(); i++) {
            System.out.println(clientHandles.get(i).socketMessage.getInetAddress() + ", At index: " + clientHandles.get(i).index + ", As: " + clientHandles.get(i).name + ", Is connected: " + clientHandles.get(i).isConnected);
        }
    }


    public static void main(String[] argv) {
        Server client = new Server();
        client.run();
    }
}