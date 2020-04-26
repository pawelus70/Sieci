import javax.swing.*;
import javax.swing.border.Border;
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

    JTextField tf = new JTextField(); // Pole do pisania wiadomości
    JButton send = new JButton("Wyślij");
    JTextArea Wiadomoscii = new JTextArea();
    JTextArea Userss = new JTextArea("Użytkownicy:");
    JTextArea Logii = new JTextArea();

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
                    //System.out.println(name + ", " + LocalTime.now().withNano(0) + ", " + message);
                    Wiadomoscii.append(name + ", " + LocalTime.now().withNano(0) + ", " + message + "\n");
                    Wiadomoscii.setCaretPosition(Wiadomoscii.getDocument().getLength());
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
            message = message.substring(3);
            for (int i = 0; i < clientHandles.size(); i++) {
                //if (i == index) continue;
                clientHandles.get(i).printMessageWriter.println(clientHandles.get(index).name + ": " + message);
                clientHandles.get(i).printMessageWriter.flush();
            }
        }else {
            clientHandles.get(index).printMessageWriter.println("Unrecognizable request");
            clientHandles.get(index).printMessageWriter.flush();

        }
    }

    public void run() {
        clientHandles = new ArrayList();   //client list

        try {

            /////////********************GUI************************////////////////

            //Obramowanie
            Border border = BorderFactory.createLineBorder(Color.BLACK);

            //Tworzenie ramki
            JFrame frame = new JFrame("Serwer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 500);


            //Tworzenie menu i komponentów
            JMenuBar mb = new JMenuBar();
            JMenu m2 = new JMenu("Pomoc"); // Przycisk pomocy
            JMenu m3 = new JMenu("Start/Stop"); // Przycisk Włączania serwera
            mb.add(m2);
            mb.add(m3);


            //Tworzenie panelu
            JPanel panel = new JPanel(); //Panel
            JLabel label = new JLabel("Napisz coś!");
            tf.setPreferredSize(new Dimension(500,50));
            tf.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10,10,10,10)));

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

            Userss.setEditable(false);
            JScrollPane Users = new JScrollPane(Userss);
            Users.setPreferredSize(new Dimension(100, 500));

            //Logi

            JScrollPane Logi = new JScrollPane(Logii);
            Logii.setEditable(false);
            Logii.setSize(new Dimension(450, 100));
            Logii.setLineWrap(true);
            Logii.setWrapStyleWord(true);

            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, panel);
            frame.getContentPane().add(BorderLayout.NORTH, mb);
            frame.getContentPane().add(BorderLayout.CENTER, Wiadomosci);
            frame.getContentPane().add(BorderLayout.WEST, Users);
            frame.getContentPane().add(BorderLayout.EAST, Logi);
            frame.setVisible(true);

            /////////********************************************////////////////

            ServerSocket serverSocket = new ServerSocket(4242);
            //System.out.println("Server online. Server version (1.0.1).\n");
            Logii.append("Server online. Server version (1.0.1).\n");

            while (true) {
                Socket clientSocket = serverSocket.accept(); //accept request

                //receive connection message
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                clientHandles.add(new ClientHandle(clientSocket, (clientHandles.size()))); //add client to database
                threadPool.execute(clientHandles.get(clientHandles.size() - 1)); //execute client thread
                clientHandles.get(clientHandles.size() - 1).name = bufferedReader.readLine(); //set client name

                //System.out.println("Connected new Client has connected.\t" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress());
                Logii.append("Connected new Client has connected.\n" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress() + "\n");
                amountConnected = clientHandles.size() - 1;
                showConnected();
                usersConnected();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisconnection(int index) {
        Logii.append("User has disconnected.\n" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientHandles.get(index).name + "\n");
        clientHandles.remove(index);
        for (int i = 0; i < clientHandles.size(); i++) {
            clientHandles.get(i).index = i;
        }
        amountConnected = clientHandles.size() - 1;
        usersConnected();
    }

    public void showConnected() {
        //System.out.println("Currently connected: ");
        Logii.append("Currently connected: \n");
        for (int i = 0; i < clientHandles.size(); i++) {
            //System.out.println(clientHandles.get(i).socketMessage.getInetAddress() + ", At index: " + clientHandles.get(i).index + ", As: " + clientHandles.get(i).name + ", Is connected: " + clientHandles.get(i).isConnected);
            Logii.append(clientHandles.get(i).socketMessage.getInetAddress() + ", At index: " + clientHandles.get(i).index + ", As: " + clientHandles.get(i).name + ", Is connected: " + clientHandles.get(i).isConnected + "\n");
        }
        Logii.setCaretPosition(Logii.getDocument().getLength());
    }

    public void usersConnected(){
        Userss.setText("Użytkownicy:");
        for (int i = 0; i < clientHandles.size(); i++) {
            Userss.append("\n" + clientHandles.get(i).name);
        }
    }


    public static void main(String[] argv) {
        Server client = new Server();
        client.run();
    }
}