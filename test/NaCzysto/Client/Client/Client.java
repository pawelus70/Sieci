package Client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Client {
    String ip = "127.0.0.1";
    int port = 4242;

    Interface anInterface = new Interface();
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    ArrayList<ClientList> connectedList = new ArrayList<>();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final ScheduledExecutorService timedExecutorPool = Executors.newScheduledThreadPool(1);


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
            threadPool.execute(new WarningReceiver());

            //set name
            whatIsYourName();
            askForConnected();
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
    //002 - zmiana nazwy użytkownika potrzebuje metody przycisku
    public class RequestListener implements ActionListener {
        String message;

        @Override
        public void actionPerformed(ActionEvent ev) {
            while (clientHandle.isConnected) {
                message = anInterface.textField.getText();
                if (!(message.equals(""))) {
                    if (message.startsWith("002")) {//change name
                        if (message.length() > 6) {
                            if (pattern.matcher(message).matches()) {
                                clientHandle.printWriter.println(message);
                                clientHandle.printWriter.flush();
                                clientHandle.name = message.substring(3);
                            } else {
                                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
                                anInterface.wiadomosci.append("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.\n");
                            }
                        } else {
                            //System.out.println("Name cant be shorter than 3 signs");
                            anInterface.wiadomosci.append("Name cant be shorter than 3 signs\n");
                        }
                    } else if (message.equals("003")) {//send to everyone

                    } else if (message.equals("004")) {//whisper

                    } else if (message.equals("/help")) {
                        anInterface.wiadomosci.append("\nInstrukcja używania:\n000 - echo\n001 - ping\n002 - change name\n003 [text] - send to everyone\n004 [text] - private message\n");
                    } else {
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    }
                }
                anInterface.textField.setText("");
                break;
            }
            if (!clientHandle.isConnected) {
                anInterface.wiadomosci.append("Brak połączenia z serwerem\n");
                anInterface.textField.setText("");
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
                anInterface.wiadomosci.setText("\nLost connection to server.");
                clientHandle.isConnected = false;
                ex.printStackTrace();
            }
        }
    }

    public void run() {
        anInterface.run();
        clientHandle.name = anInterface.name;
        connectToServer(ip, port);
        clientHandle.showStatus();


        anInterface.send.addActionListener(new sendToAll());
        anInterface.textField.addActionListener(new sendToAll());
        anInterface.m21.addActionListener(new connectToServer());
        anInterface.m22.addActionListener(new checkConnection());
    }

    //003 -send to all
    public class sendToAll implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String message = "003" + anInterface.textField.getText();
            anInterface.textField.setText("");
            if(!(message.length()<4)) {
                clientHandle.printWriter.println(message);
                clientHandle.printWriter.flush();
            }
        }
    }
    //echo 000
    public class checkConnection implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (clientHandle.isConnected) {
                String message = "000";
                message += System.nanoTime();
                clientHandle.printWriter.println(message);
                clientHandle.printWriter.flush();
            } else {
                anInterface.wiadomosci.append("Check your connection with server.\n");
            }
        }
    }

    public class connectToServer implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            connectToServer(ip, port);
        }
    }

    public void showConnected() {
        anInterface.users.setText("Users:\n");
        for (ClientList clientList : connectedList) {
            //System.out.println(clientList.name + " #" + clientList.index);
            anInterface.users.append(clientList.name + "@" + clientList.index + "\n");
        }
    }

    //msg 001 period 5sec
    public void askForConnected() {
        final Runnable caller = new Runnable() {
            public void run() {
                clientHandle.printWriter.println("001");
                clientHandle.printWriter.flush();
            }
        };

        final ScheduledFuture<?> scheduledFuture = timedExecutorPool.scheduleAtFixedRate(caller, 0, 5, TimeUnit.SECONDS);
        timedExecutorPool.schedule(caller, 0, TimeUnit.SECONDS);
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.run();
    }

}