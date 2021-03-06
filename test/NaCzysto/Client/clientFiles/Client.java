/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/

package clientFiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Plik z glownymi operacjami u klienta.
 * Glowne konfiguracje jak ip, port, a takze listy
 * @see #ip
 * @see #port
 *
 * @author Created by Gabriel Cwiek and Pawel Blak
 * Last update date: 24.05.2020
 */
public class Client {
    String ip = "127.0.0.1";
    int port = 49152;

    public Interface anInterface = new Interface();
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    ArrayList<ClientList> connectedList = new ArrayList<>();
    ArrayList<MessageField> messageFields = new ArrayList<>();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private final ScheduledExecutorService timedExecutorPool = Executors.newScheduledThreadPool(1);

    /**
     * Lista klientow - nick i indeks
     * @see #name
     * @see #index
     */
    public class ClientList {
        String name;
        int index;

        ClientList(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }

    /**
     * Uchwyt na klienta
     * Nick:
     * @see #name
     * Socket:
     * @see #socket
     * Bufor:
     * @see #bufferedReader
     * "Pisarz":
     * @see #printWriter
     * Polaczenie:
     * @see #isConnected
     * "Czytacz";
     * @see #inputStreamReader
     * Unikalne ID:
     * @see #userUniqueId
     */
    public class ClientHandle {
        String name;
        Socket socket;
        BufferedReader bufferedReader;
        PrintWriter printWriter;
        Boolean isConnected = false;
        InputStreamReader inputStreamReader;
        int userUniqueId;

        /**
         * Pokazanie statusu
         */
        public void showStatus() {
            anInterface.userStatus.setText("You are connected: " + this.isConnected + "\nShown as: " + this.name + "#" + this.userUniqueId);
        }
    }

    ClientHandle clientHandle = new ClientHandle();

    /**
     *Polaczenie z serwerem
     * IP:
     * @param ip - ip
     * PORT:
     * @param port - port
     */
    public void connectToServer(String ip, int port) {
        if (!clientHandle.isConnected) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                clientHandle.showStatus();
                clientHandle.socket = new Socket(ip, port);
                clientHandle.isConnected = true;
                //text
                clientHandle.inputStreamReader = new InputStreamReader(clientHandle.socket.getInputStream());
                clientHandle.bufferedReader = new BufferedReader(clientHandle.inputStreamReader);
                clientHandle.printWriter = new PrintWriter(clientHandle.socket.getOutputStream());
                threadPool.execute(new WarningReceiver());
                clientHandle.isConnected = true;
                //set name
                whatIsYourName();
                askForConnected();
                clientHandle.showStatus();
                messageFields.get(0).messageField.append("Connected with server\n");
            } catch (Exception e) {
                messageFields.get(0).messageField.append("Can't connect with server\n");
                clientHandle.socket = null;
                clientHandle.isConnected = false;
                clientHandle.printWriter = null;
                clientHandle.bufferedReader = null;
                clientHandle.inputStreamReader = null;
                clientHandle.isConnected = false;
            }
        } else {
            messageFields.get(0).messageField.append("You are already connected.\n");
        }
    }

    /**
     * Nazwa uzytkownika
     */
    public void whatIsYourName() {
        clientHandle.printWriter.println(clientHandle.name);
        clientHandle.printWriter.flush();

        try {
            clientHandle.userUniqueId = Integer.decode(clientHandle.bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Odczytywanie wiadomosci
     * Czytanie po otrzymanej wartosci kodu
     * Wiadmosc:
     * @see #message
     */
    public class WarningReceiver implements Runnable {
        String message;

        public void run() {

            try {
                while (clientHandle.bufferedReader != null) {
                    message = clientHandle.bufferedReader.readLine();
                    if (message.startsWith("000")) {
                        message = message.substring(3);
                        long time = Long.parseLong(message) / 1000000;
                        messageFields.get(0).messageField.append("Your ping is: " + time + " ms\n");
                    } else if (message.startsWith("001")) {
                        connectedList.clear();
                        message = message.substring(3);
                        for (int i = 0; i < Integer.decode(message); i++) {
                            connectedList.add(new ClientList(clientHandle.bufferedReader.readLine(), Integer.parseInt(clientHandle.bufferedReader.readLine())));
                        }
                        showConnected();
                    } else if(message.startsWith("004")){
                        message=message.substring(3).trim();

                        messageFields.get(0).messageField.append(message + "\n");
                        messageFields.get(0).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                        messageFields.get(2).messageField.append(message + "\n");
                        messageFields.get(2).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                    }else if(message.startsWith("005")){
                        message=message.substring(3).trim();
                        messageFields.get(0).messageField.append(message + "\n");
                        messageFields.get(0).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                        messageFields.get(2).messageField.append(message + "\n");
                        messageFields.get(2).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                    }
                    else {
                        messageFields.get(0).messageField.append(message + "\n");
                        messageFields.get(0).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                        messageFields.get(1).messageField.append(message + "\n");
                        messageFields.get(1).messageField.setCaretPosition(anInterface.messagesField.getDocument().getLength());
                    }
                }
            } catch (IOException ex) {
                messageFields.get(0).messageField.append("\nLost connection to server.\n");
                clientHandle.isConnected = false;
                ex.printStackTrace();
            }
        }
    }

    /**
     * Wykonywanie
     */
    public void run() {
        anInterface.run();


        messageFields.add(new MessageField());
        messageFields.get(0).createTab();

        anInterface.messageTabs.addTab("All", messageFields.get(0).messageScroll);
        anInterface.messageTabs.setBackgroundAt(0, java.awt.Color.decode(anInterface.buttonColor));

        messageFields.add(new MessageField());
        messageFields.get(1).createTab();

        anInterface.messageTabs.addTab("All Chat", messageFields.get(1).messageScroll);
        anInterface.messageTabs.setBackgroundAt(1, java.awt.Color.decode(anInterface.buttonColor));

        messageFields.add(new MessageField());
        messageFields.get(2).createTab();

        anInterface.messageTabs.addTab("Whispers", messageFields.get(2).messageScroll);
        anInterface.messageTabs.setBackgroundAt(2, java.awt.Color.decode(anInterface.buttonColor));

        clientHandle.name = anInterface.name = anInterface.inputBox(true);

        anInterface.sendButton.addActionListener(new sendToAll());
        anInterface.textField.addActionListener(new sendToAll());
        anInterface.m21.addActionListener(new connectToServer());
        anInterface.m22.addActionListener(new checkConnection());
        anInterface.m31.addActionListener(new helpMe());
        anInterface.m32.addActionListener(new credits());
        anInterface.customSquare.addMouseListener(new changeUserName());
        anInterface.m23.addActionListener(new changeIpPort());
        connectToServer(ip, port);
    }

    //echo 000

    /**
     * Sprawdzanie polaczenia czyt. ping
     */
    public class checkConnection implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (clientHandle.isConnected) {
                String message = "000";
                message += System.nanoTime();
                clientHandle.printWriter.println(message);
                clientHandle.printWriter.flush();
            } else {
                anInterface.messagesField.append("Check your connection with server.\n");
                messageFields.get(0).messageField.append("Check your connection with server.\n");
            }
        }
    }

    /**
     * Wykonawcy
     */
    public class credits implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            messageFields.get(0).messageField.append("**********Credits**********\nGabriel Cwiek - Biedny Student\nPawel Blak - BiedniejszyStudent\n**********Credits**********\n");
        }
    }

    /**
     * Pomoc
     */
    public class helpMe implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            messageFields.get(0).messageField.append("*********\nWitaj,\nAby polaczyc sie ponownie z serwerem przejdz do Server>Try connect to server\nAby sprawdzic ping Server>Check ping\nAby wyslac wiadomosc do wszystkich napisz w dolnym okienku i kliknij enter\nAby wyslac wiadomosc prywatna musisz wpisac dane osoby w tym schemacie: @nick#ID: (twoja wiadomosc)\nPo lewej stronie znajduja sie aktualnie polączeni uzytkownicy\nNick mozesz zmienic klikajac w lewym gornym rogu na odpowiadajace okno i wpisujac nowy nick\nZakladki chatu znajduja sie nad oknem z wiadomosciami\n*********\n");
        }
    }

    /**
     * Zmiana IpPort
     */
    public class changeIpPort implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            ip = anInterface.changeIpPort(ip);
        }
    }
    //msg 001 period 5sec

    /**
     * Prosba o polaczenie
     */
    public void askForConnected() {
        final Runnable caller = new Runnable() {
            public void run() {
                clientHandle.printWriter.println("001");
                clientHandle.printWriter.flush();
            }
        };

        final ScheduledFuture<?> scheduledFuture = timedExecutorPool.scheduleAtFixedRate(caller, 1, 3, TimeUnit.SECONDS);
        timedExecutorPool.schedule(caller, 0, TimeUnit.SECONDS);
    }

    //msg 002 - change user name

    /**
     * Zmien nazwe uzytkownika
     */
    public class changeUserName implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            String tempName;
            tempName = anInterface.inputBox(false);
            if (!(tempName.length() < 4)) {
                clientHandle.name = tempName;
                clientHandle.showStatus();
                if (clientHandle.isConnected) {
                    clientHandle.printWriter.println("002" + clientHandle.name);
                }
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //003 -sendButton to all + whisper

    /**
     * Wyslij wiadomosc do kazdej osoby
     */
    public class sendToAll implements ActionListener {
        /**
         * Sprawdzanie wiadomosci itd.
         * @param e - active
         */
        public void actionPerformed(ActionEvent e) {
            if (clientHandle.isConnected) {
                String message = anInterface.textField.getText();
                String userIDStart = "" + message.indexOf("#");
                String userIDStop = "" + message.indexOf(":");
                String targetID;
                message = message.trim();
                if(!(message.startsWith("@"))){
                    anInterface.textField.setText("");
                    clientHandle.printWriter.println("003" + message);
                    clientHandle.printWriter.flush();
                }else{
                    if (!(userIDStart.equals("-1") && userIDStop.equals("-1"))) {
                        Pattern pattern = Pattern.compile("\\d+");
                        targetID = message.substring(message.indexOf("#") + 1, message.indexOf(":")).trim();
                        if (pattern.matcher(targetID).matches()) {
                            messageFields.get(0).messageField.append("Whispered to: " + message.trim()+"\n");
                            messageFields.get(2).messageField.append("To: " + message.trim()+"\n");
                            clientHandle.printWriter.println("004" + message.trim());
                        } else {
                            messageFields.get(0).messageField.append("Wrong format of message. Try again\n");
                        }
                        //String message = "003" + anInterface.textField.getText();
                    } else  {
                        messageFields.get(0).messageField.append("Wrong format of message. Try again\n");
                    }
                }

            } else {
                messageFields.get(0).messageField.append("Check your connection with server.\n");
            }
        }
    }

    /**
     * Polaczenie z serwerem reczne
     */
    public class connectToServer implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!clientHandle.isConnected) {
                connectToServer(ip, port);
            } else {
                messageFields.get(0).messageField.append("You are already connected");
            }

        }
    }

    /**
     * Pokaz polaczonych uzytkownikow
     */
    public void showConnected() {
        anInterface.users.setText("Users:\n");
        for (int i = 0; i < connectedList.size(); i++) {
            //System.out.println(clientList.name + " #" + clientList.index);
            if (connectedList.get(i).name.equals(clientHandle.name)) {
                if(connectedList.get(i).index==clientHandle.userUniqueId)
                continue;
            }
            anInterface.users.append(connectedList.get(i).name +"#"+ connectedList.get(i).index + "\n");
        }
    }

    /**
     * Rozpocznij program
     */
    public static void main(String[] argv) {
        Client client = new Client();
        client.run();
    }

}
/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/
