package Client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import Client.Interface;

public class Client {
    String ip = "127.0.0.1";
    int port = 4242;

    Interface anInterface = new Interface();
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
            //System.out.println("To get list of commands type: \"help\"");
            anInterface.wiadomosci.append("To get list of commands type: \"/help\"\n");
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
            //System.out.println("Can't connect with server.");
            anInterface.wiadomosci.append("Can't connect with server\n");
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


//TODO: akcje muszą mieć odobny przycisk.
    public class RequestListener implements ActionListener {
        String message;

        @Override
        public void actionPerformed(ActionEvent ev) {
            while (clientHandle.isConnected) {
                message = anInterface.tf.getText();
                if (!(message.equals(""))) {
                    if (message.startsWith("000")) { //echo
                        message += System.nanoTime();
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if (message.startsWith("001")) { //client list
                        anInterface.wiadomosci.append("Lista aktywnych użytkowników: \n");
                        clientHandle.printWriter.println("001");
                        clientHandle.printWriter.flush();
                    } else if (message.startsWith("002")) {//change name
                        if(message.length()>6){
                            if(pattern.matcher(message).matches()){
                                clientHandle.printWriter.println(message);
                                clientHandle.printWriter.flush();
                                clientHandle.name = message.substring(3);
                            }else{
                                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
                                anInterface.wiadomosci.append("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.\n");
                            }
                        }else {
                            //System.out.println("Name cant be shorter than 3 signs");
                            anInterface.wiadomosci.append("Name cant be shorter than 3 signs\n");
                        }
                    } else if (message.equals("003")) {//send to everyone
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if(message.equals("004")) {//whisper

                    }else if(message.equals("/help")) {
                        anInterface.wiadomosci.append("\nInstrukcja używania:\n000 - echo\n001 - ping\n002 - change name\n003 [text] - send to everyone\n004 [text] - private message\n");
                    }else{
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    }
                }
                anInterface.tf.setText("");
                break;
            }
            if(!clientHandle.isConnected) {
                anInterface.wiadomosci.append("Brak połączenia z serwerem\n");
                anInterface.tf.setText("");
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
                        anInterface.wiadomosci.append("Ping: " + time + " ms\n");
                        //System.out.println("Ping: " + time + " ms");
                    } else if (message.startsWith("001")) {
                        connectedList.clear();
                        message = message.substring(3);
                        for (int i = 0; i < Integer.decode(message); i++) {
                            connectedList.add(new ClientList(clientHandle.bufferedReader.readLine(), Integer.parseInt(clientHandle.bufferedReader.readLine())));
                        }
                        showConnected();
                    } else {
                        //System.out.println(message);
                        anInterface.wiadomosci.append(message + "\n");
                        anInterface.wiadomosci.setCaretPosition(anInterface.wiadomosci.getDocument().getLength());
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run(){
        anInterface.run();
        clientHandle.name=anInterface.name;
        connectToServer(ip, port);
        clientHandle.showStatus();
        //threadPool.execute(new RequestListener());
        threadPool.execute(new WarningReceiver());
        anInterface.send.addActionListener(new RequestListener());
        anInterface.tf.addActionListener(new RequestListener());
    }



    public void showConnected() {
        for (ClientList clientList : connectedList) {
            //System.out.println(clientList.name + " #" + clientList.index);
            anInterface.wiadomosci.append(clientList.name + " #" + clientList.index + "\n");
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.run();



    }

}