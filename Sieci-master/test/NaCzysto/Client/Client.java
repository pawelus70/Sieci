import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
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
    Pattern pattern =Pattern.compile("^[a-zA-Z0-9]+$");
    ArrayList<ClientList> connectedList = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    JTextField tf = new JTextField(); // Pole do pisania wiadomości
    JButton send = new JButton("Wyślij");
    JTextArea Wiadomoscii = new JTextArea();

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
            Wiadomoscii.append("To get list of commands type: \"/help\"\n");
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
            Wiadomoscii.append("Can't connect with server\n");
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
            //System.out.println("Enter your name: ");
            while(true) {
                name =JOptionPane.showInputDialog(null, "Wprowadź swój pseudonim:", "Wprowadzenie", JOptionPane.QUESTION_MESSAGE); /*new BufferedReader(new InputStreamReader(System.in)).readLine()*/;
                if(pattern.matcher(name).matches()){
                    if(!(name.length()<3))break;
                }
                JOptionPane.showMessageDialog(null, "Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.", "Popraw", JOptionPane.ERROR_MESSAGE);
                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
            }

            /////////********************GUI************************////////////////

            //Obramowanie
            Border border = BorderFactory.createLineBorder(Color.BLACK);

            //Tworzenie ramki
            JFrame frame = new JFrame("Komunikator " + name);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);


            //Tworzenie menu i komponentów
            JMenuBar mb = new JMenuBar();
            JMenu m1 = new JMenu("Plik"); //Przycisk Plik
            JMenu m2 = new JMenu("Pomoc"); // Przycisk pomocy
            mb.add(m1);
            mb.add(m2);
            JMenuItem m11 = new JMenuItem("Otwórz"); //Pod opcje w Plik
            JMenuItem m22 = new JMenuItem("Zapisz jako...");
            m1.add(m11);
            m1.add(m22);

            //Tworzenie panelu
            JPanel panel = new JPanel(); //Panel
            JLabel label = new JLabel("Napisz coś!");

            tf.setPreferredSize(new Dimension(500,50));
            tf.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10,10,10,10)));
            send.addActionListener(new RequestListener());
            tf.addActionListener(new RequestListener());


            // panel.add(Users);
            panel.add(label); //Dodawanie wszystko we flowLayout
            panel.add(tf);
            panel.add(send);

            // Głowny obszar tekstowy do wiadomości

            JScrollPane Wiadomosci = new JScrollPane(Wiadomoscii);
            Wiadomoscii.setEditable(false);
            Wiadomoscii.setLineWrap(true);
            Wiadomoscii.setWrapStyleWord(true);
            // Wiadomoscii.setSize(new Dimension(600,400));
            Wiadomoscii.setSize(new Dimension(250,400));
            Wiadomoscii.setBackground(new Color(245, 245, 245));

            //Użytkownicy
            JTextArea Userss = new JTextArea("Użytkownicy:");
            Userss.setEditable(false);
            JScrollPane Users = new JScrollPane(Userss);
            Users.setPreferredSize(new Dimension(100, 500));


            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, panel);
            frame.getContentPane().add(BorderLayout.NORTH, mb);
            frame.getContentPane().add(BorderLayout.CENTER, Wiadomosci);
            frame.getContentPane().add(BorderLayout.WEST, Users);
            frame.setVisible(true);

            /////////********************************************////////////////

            clientHandle.name=name;
            connectToServer(ip, port);
            clientHandle.showStatus();
            //threadPool.execute(new RequestListener());
            threadPool.execute(new WarningReceiver());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class RequestListener implements ActionListener {
        String message;

        @Override
        public void actionPerformed(ActionEvent ev) {
            while (clientHandle.isConnected) {
                message = tf.getText();
                if (!(message.equals(""))) {
                    if (message.startsWith("000")) { //echo
                        message += System.nanoTime();
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if (message.startsWith("001")) { //client list
                        Wiadomoscii.append("Lista aktywnych użytkowników: \n");
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
                                Wiadomoscii.append("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.\n");
                            }
                        }else {
                            //System.out.println("Name cant be shorter than 3 signs");
                            Wiadomoscii.append("Name cant be shorter than 3 signs\n");
                        }
                    } else if (message.equals("003")) {//send to everyone
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    } else if(message.equals("004")) {//whisper

                    }else if(message.equals("/help")) {
                        Wiadomoscii.append("\nInstrukcja używania:\n000 - echo\n001 - ping\n002 - change name\n003 [text] - send to everyone\n004 [text] - private message\n");
                    }else{
                        clientHandle.printWriter.println(message);
                        clientHandle.printWriter.flush();
                    }
                }
                tf.setText("");
                break;
            }
            if(clientHandle.isConnected == false) {
                Wiadomoscii.append("Brak połączenia z serwerem\n");
                tf.setText("");
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
                        Wiadomoscii.append("Ping: " + time + " ms\n");
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
                        Wiadomoscii.append(message + "\n");
                        Wiadomoscii.setCaretPosition(Wiadomoscii.getDocument().getLength());
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showConnected() {
        for (ClientList clientList : connectedList) {
            //System.out.println(clientList.name + " #" + clientList.index);
            Wiadomoscii.append(clientList.name + " #" + clientList.index + "\n");
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.run();



    }

}