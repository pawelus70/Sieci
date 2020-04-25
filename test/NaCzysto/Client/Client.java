import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.regex.Pattern;


public class Client {
    String ip = "127.0.0.1";
    int port = 4242;
    Pattern pattern =Pattern.compile("^[a-zA-Z0-9]+$");
    ArrayList<ClientList> connectedList = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);


    public class ClientList {
        String name;
        int index;

        ClientList(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }


    public class ClientHandle {
        String name;
        Socket socket;
        BufferedReader bufferedReader;
        PrintWriter printWriter;
        Boolean isConnected;
        InputStreamReader inputStreamReader;
        int index;

        public void showStatus() {
            System.out.println("Is Connected: " + this.isConnected);
            System.out.println("Connected as: " + this.name);
            System.out.println("Your index is: " + this.index);
            System.out.println("To get list of commands type: \"help\"");
        }
    }

    ClientHandle clientHandle = new ClientHandle();


    public void connectToServer(String ip, int port) {
        try {
            clientHandle.socket = new Socket(ip, port);
            clientHandle.isConnected = true;
            //text
            clientHandle.inputStreamReader = new InputStreamReader(clientHandle.socket.getInputStream());
            clientHandle.bufferedReader = new BufferedReader(clientHandle.inputStreamReader);
            clientHandle.printWriter = new PrintWriter(clientHandle.socket.getOutputStream());


            //set name
            whatIsYourName();
        } catch (Exception e) {
            System.out.println("Can't connect with server.");
            clientHandle.socket = null;
            clientHandle.isConnected = false;
            clientHandle.printWriter = null;
            clientHandle.bufferedReader = null;
            clientHandle.inputStreamReader = null;
        }
    }

    public void whatIsYourName() {
        clientHandle.printWriter.println(clientHandle.name);
        clientHandle.printWriter.flush();

        try {
            clientHandle.index = Integer.decode(clientHandle.bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            String name;
            System.out.println("Enter your name: ");
            while(true) {
                name = new BufferedReader(new InputStreamReader(System.in)).readLine();
                if(pattern.matcher(name).matches()){
                    if(!(name.length()<3))break;
                }
                System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
            }
            clientHandle.name=name;
            connectToServer(ip, port);
            clientHandle.showStatus();
            threadPool.execute(new RequestListener());
            threadPool.execute(new WarningReceiver());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class RequestListener implements Runnable {
        String message;

        public void run() {
            while (clientHandle.isConnected) {
                try {
                    message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!(message.equals(""))) {
                    if (message.startsWith("000")) { //echo
                        message += System.nanoTime();
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if (message.startsWith("001")) { //client list
                        clientHandle.printWriter.println("001");
                        clientHandle.printWriter.flush();
                    } else if (message.startsWith("002")) {//change name
                        if(message.length()>6){
                            if(pattern.matcher(message).matches()){
                                clientHandle.printWriter.println(message);
                                clientHandle.printWriter.flush();
                                clientHandle.name = message.substring(3);
                            }else{
                                System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
                            }
                        }else {
                            System.out.println("Name cant be shorter than 3 signs");
                        }
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if (message.equals("003")) {//send to everyone
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if(message.equals("004")) {//whisper

                    }else{
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    }
                }
            }
        }
    }

    public class WarningReceiver implements Runnable {
        String message;

        public void run() {
            try {
                while (clientHandle.bufferedReader != null) {
                    message = clientHandle.bufferedReader.readLine();
                    if (message.startsWith("000")) {
                        message = message.substring(3);
                        long time = Long.parseLong(message) / 1000000;

                        System.out.println("Ping: " + time + " ms");
                    } else if (message.startsWith("001")) {
                        connectedList.clear();
                        message = message.substring(3);
                        for (int i = 0; i < Integer.decode(message); i++) {
                            connectedList.add(new ClientList(clientHandle.bufferedReader.readLine(), Integer.parseInt(clientHandle.bufferedReader.readLine())));
                        }
                        showConnected();

                    } else {
                        System.out.println(message);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showConnected() {
        for (ClientList clientList : connectedList) {
            System.out.println(clientList.name + " #" + clientList.index);
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.run();
    }
}