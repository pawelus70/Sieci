/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/


package serverFiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Server {
    ArrayList<ClientHandle> clientHandles; //Lista użytkowników
    int userUniqueID = 1; //Liczba połączeń
    int usersIndex = 1; //Index użytkowników
    // private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService timedExecutorPool = Executors.newScheduledThreadPool(1); //Executory
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    Interface anInterface = new Interface(); //Interfejs dołącz

    public void ShareClientList(int index) { //Lista użytkowników
        clientHandles.get(index).printMessageWriter.println("001" + clientHandles.size()); //indexy
        clientHandles.get(index).printMessageWriter.flush(); //przekazanie
        for (int i = 0; i < clientHandles.size(); i++) { //WYpisz wszystko
            clientHandles.get(index).printMessageWriter.println(clientHandles.get(i).name);
            clientHandles.get(index).printMessageWriter.flush();
            clientHandles.get(index).printMessageWriter.println(clientHandles.get(i).userUniqueID);
            clientHandles.get(index).printMessageWriter.flush();
        }
    }

    public class ClientHandle implements Runnable {//Tak zwany uchwyt Użytkownika
        String name;//nick
        int index;//index
        Long time;//czas
        Socket socketMessage;//wiadomość socketu
        BufferedReader bufferedMessageReader;//Czytnik
        InputStreamReader inputMessageStreamReader;//Czytacz
        Boolean isConnected = false;//Połączenie
        PrintWriter printMessageWriter;//Pisarz
        int userUniqueID;


        public ClientHandle(Socket clientSocket, int index, int userUniqueID) {//Uzytkownik info
            try {
                socketMessage = clientSocket;
                printMessageWriter = new PrintWriter(socketMessage.getOutputStream());
                inputMessageStreamReader = new InputStreamReader((socketMessage.getInputStream()));
                bufferedMessageReader = new BufferedReader(inputMessageStreamReader);
                isConnected = true;
                this.index = index;
                printMessageWriter.println(index);
                printMessageWriter.flush();
                this.userUniqueID = userUniqueID;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {//wykonywanie
            String message;//wiadomość
            this.index = clientHandles.indexOf(this);//przypisanie indexu
            try {
                while ((message = bufferedMessageReader.readLine()) != null) {
                    time = System.nanoTime();//czas
                    //System.out.println(name + ", " + LocalTime.now().withNano(0) + ", " + message);
                    anInterface.wiadomosci.append(name + ", " + LocalTime.now().withNano(0) + ", " + message + "\n");//dodawanie wiadomości
                    anInterface.wiadomosci.setCaretPosition(anInterface.wiadomosci.getDocument().getLength());//Ustawianie "karetki"
                    requestListener(message, clientHandles.indexOf(this));//wiad
                }
            } catch (IOException e) {
                this.isConnected = false;
                e.printStackTrace();
                onDisconnection(clientHandles.indexOf(this));
                clientHandles.remove(this);//Usuń
            }
        }

    }


    public void requestListener(String message, int index) {//Nasłuchiwanie i warunki kodów
        if (message.startsWith("000")) { //Echo

            String TempTime = message.substring(3);
            clientHandles.get(index).printMessageWriter.println("000" + (System.nanoTime() - Long.parseLong(TempTime)));
            clientHandles.get(index).printMessageWriter.flush();


        } else if (message.startsWith("001")) {//Podaj liste użytkowników
            ShareClientList(index);
        } else if (message.startsWith("002")) {//Zmień nick
            clientHandles.get(index).name = message.substring(3) + "#" + clientHandles.get(index).userUniqueID;
        } else if (message.startsWith("003")) {//Wyślij każdemu
            message = message.substring(3);
            for (int i = 0; i < clientHandles.size(); i++) {
                if (message.startsWith("Server: ")) {
                    clientHandles.get(i).printMessageWriter.println(message);
                    clientHandles.get(i).printMessageWriter.flush();
                } else {
                    if (i == index) {
                        clientHandles.get(i).printMessageWriter.println("You: " + message);
                        clientHandles.get(i).printMessageWriter.flush();
                        continue;
                    } else {
                        clientHandles.get(i).printMessageWriter.println(clientHandles.get(index).name + ": " + message);
                        clientHandles.get(i).printMessageWriter.flush();
                    }
                }
            }
        } else if (message.startsWith("004")) {
            message = message.substring(4);

            String user = message.substring(0,message.indexOf("#")).trim();
            String userID = message.substring(message.indexOf("#")+1,message.indexOf(":")).trim();
            int temp= Integer.decode(userID); //zmiana string ID na Int ID
            boolean gotMatch=false;
            for(int i=0;i<clientHandles.size();i++){
                if(user.equals(clientHandles.get(i).name)){
                    if(temp==clientHandles.get(i).userUniqueID)
                    gotMatch = true;
                    clientHandles.get(i).printMessageWriter.println("005From: " +clientHandles.get(index).name+"#"+clientHandles.get(index).userUniqueID+": "+ message.substring(message.indexOf(":")+1).trim());
                    clientHandles.get(i).printMessageWriter.flush();
                    break;
                }
            }
            if(!gotMatch){
                clientHandles.get(index).printMessageWriter.println("004 Cannot find user with such name or index");
                clientHandles.get(index).printMessageWriter.flush();
            }
        } else {
            clientHandles.get(index).printMessageWriter.println("Unrecognizable request");
            clientHandles.get(index).printMessageWriter.flush();

        }
    }

    public void run() {
        clientHandles = new ArrayList();   //Lista klientów
        anInterface.run(); //Interfejs
        anInterface.sendButton.addActionListener(new ServerSendButton());
        anInterface.tf.addActionListener(new ServerSendButton());
        showConnected();//Użytkownicy

        try {
            ServerSocket serverSocket = new ServerSocket(49152);//Otwórz socket
            //System.out.println("Server online. Server version (1.0.1).\n");
            anInterface.wiadomosci.append("Server online. Server version (1.0.1).\n");//Info

            while (true) {
                Thread.sleep(10);//Wątek
                Socket clientSocket = serverSocket.accept(); //Akceptuj żądanie

                //Odbieranie wiadomości połączenia
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                clientHandles.add(new ClientHandle(clientSocket, usersIndex, userUniqueID)); //Dodaj klienta do bazy
                threadPool.execute(clientHandles.get(clientHandles.size() - 1)); //Wykonaj wątek klienta
                clientHandles.get(clientHandles.size() - 1).name = bufferedReader.readLine(); //pobierz imię klienta


                usersIndex += 1; //I index

                //System.out.println("Connected new Client has connected.\t" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress());
                anInterface.logii.append("Connected new Client has connected.\n" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress() + "\n");//Powiadomienie
                userUniqueID += 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisconnection(int index) {//Gdy rozłączenie
        anInterface.logii.append("User has disconnected, " + LocalDate.now() + ", " + LocalTime.now().withNano(0) + ",  " + clientHandles.get(index).name + "@" + clientHandles.get(index).index + "\n");


    }


    public void showConnected() {//Ilu użytkoników
        final Runnable caller = new Runnable() {
            @Override
            public void run() {
                anInterface.userss.setText("Users: " + (clientHandles.size())); //info o rozmiarze
                for (int i = 0; i < clientHandles.size(); i++) {
                    anInterface.userss.append("\n" + clientHandles.get(i).name + "@" + i);//Wypis
                }
            }
        };
        final ScheduledFuture<?> scheduledFuture = timedExecutorPool.scheduleAtFixedRate(caller, 1, 1, TimeUnit.SECONDS);
        timedExecutorPool.schedule(caller, 0, TimeUnit.SECONDS);
    }

    public class ServerSendButton implements ActionListener {
        String message;


        public void actionPerformed(ActionEvent e) {
            message = "003Server: " + anInterface.tf.getText() + "\n";
            anInterface.wiadomosci.append(message);
            requestListener(message, -1);
            anInterface.tf.setText("");

        }
    }

    public static void main(String[] argv) {//Start serwer
        Server client = new Server();
        client.run();
    }
}
/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/
