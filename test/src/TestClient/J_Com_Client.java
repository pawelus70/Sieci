import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


//komentarz
public class J_Com_Client {
    JTextArea textArea; //pole odebranych wiadomości
    JTextField messageField; //pole wiadomości do wysłania
    BufferedReader buffer; //buffer
    PrintWriter writer; //"pisarz" wiadomości
    Socket clientSocket; //adres i port serwera

    String nickname;
    boolean isConnected= false; //flaga czy zostało nawiązane połączenie

    //utwórz okno

    public  void createWorksheet(String n){
        nickname = n;

        JFrame border = new JFrame("Klient"); //nazwa aplikacji
        JPanel mainInterface = new JPanel();

        textArea = new JTextArea(15,60); //miejsce do wyświetlania tekstu
        textArea.setLineWrap(true); //zawijanie wierszy
        textArea.setWrapStyleWord(true); //zawijaj całe słowa
        textArea.setEditable(false);    //możliwość edycji tekstu w polu

        JScrollPane scrollPane = new JScrollPane(textArea); //scroll
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); //zawsze pionowo
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //nigdy poziomo

        messageField = new JTextField(45); //pole do wpisywania tekstu

        JButton connectButton = new JButton("Connect"); //przycisk do połączenia z serwerem
        JButton sendButton = new JButton("Send"); //przycisk wyślij
        sendButton.addActionListener(new SendButtonListener()); //dodanie do niego akcji
        messageField.addActionListener(new SendButtonListener());//wyślij enter

        connectButton.addActionListener(new ConnectButtonListener());//dodanie akcji
        //dodanie elementów
        mainInterface.add(connectButton);
        mainInterface.add(scrollPane);
        mainInterface.add(messageField);
        mainInterface.add(sendButton);


        //utwórz wątek dla klienta
        clientStart();

        border.getContentPane().add(BorderLayout.CENTER,mainInterface);
        border.setSize(720,400);
        border.setVisible(true);
       // border.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        border.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        border.addWindowListener(new WindowAdapter() {// na zamknięcie okna
            @Override
            public void windowClosing(WindowEvent arg0){ //nasłuchuj zamknięcia
                System.exit(0);
                /*try {
                    writer.close();
                    buffer.close();
                    clientSocket.close();//Zakończ połączenie
                   System.exit(0); //Zamknij proces
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
            }
        });
        if(clientSocket.isConnected()) {
            border.addWindowListener(new WindowAdapter() {// na zamknięcie okna
                @Override
                public void windowClosing(WindowEvent arg0) { //nasłuchuj zamknięcia
                    writer.println("Koniec");
                    writer.flush();
                    System.exit(0);
                /*try {
                    writer.close();
                    buffer.close();
                    clientSocket.close();//Zakończ połączenie
                   System.exit(0); //Zamknij proces
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                }
            });
        }else{
                System.exit(0);
            }
        }
    //żądanie połączenia z serwerem funkcja próbująca połączyć od nowa, to samo co w settup
    public void connectToServer(){
        if(isConnected==true){
            textArea.setText(null);
            textArea.append("You are already connected");
        }
        else{
        boolean success = false;
        try {
            clientSocket = new Socket("127.0.0.1", 4242); //Adres IP hosta w tym przypadku 192.168.8.106 dla testw między urządzeniami. Do testów zastąpić 127.0.0.1
            InputStreamReader serwerReader = new InputStreamReader(clientSocket.getInputStream());
            buffer = new BufferedReader(serwerReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
            success= true;
            System.out.println("gotowe do użycia");
            textArea.append("Połączono jako: " + nickname + "\n"); //Wiadomość o połączeniu
        }catch(IOException e) {
            textArea.setText(null);
            clientSocket = null;
            buffer = null;
            textArea.append("Brak połączenia z serverem. Spróbuj ponownie za kilka minut\n");
            e.printStackTrace();
        }
        if(success==true)isConnected=true;
        }
    }
    //setup klienta
    public void clientStart(){
            //Próba połączenia z serwerem
            textArea.setText(null);
            boolean success = false; //czy udało się połączyć
            try {
            clientSocket = new Socket("127.0.0.1", 4242); //Adres IP hosta w tym przypadku 192.168.8.106 dla testw między urządzeniami. Do testów zastąpić 127.0.0.1
            InputStreamReader serwerReader = new InputStreamReader(clientSocket.getInputStream()); //stworzenie buffera, piszrza i czytelnika ze strumienia
            buffer = new BufferedReader(serwerReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("gotowe do użycia");
            textArea.append("Połączono jako: " + nickname + "\n"); //Wiadomość o połączeniu
            success= true; //jeśli się uda
        }catch(IOException e) {
            clientSocket = null;
            buffer = null;
            textArea.append("Brak połączenia z serverem. Spróbuj ponownie za kilka minut\n");
            e.printStackTrace();
        }
            Thread clientThread = new Thread(new warningsReceiver());
            clientThread.start();
        if(success==true)isConnected=true; //ustaw flagę połączono

    }
    //przycisk połącz z serwerem
    public class ConnectButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            connectToServer();
        }
    }
    //przycisk wyślij
    public class SendButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            LocalTime time = LocalTime.now();//pobierz czas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);//formatuj czas
            String czas = formatter.format(time);//przypisz czas do string
            try{
                writer.println("("+czas+") "+nickname + ": " + messageField.getText());
                writer.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            messageField.setText("");
            messageField.requestFocus();
        }
    }
    //odbieranie wiadomości
    public class warningsReceiver implements Runnable{
        public void run() {
            String message;
            try{
                while((message = buffer.readLine()) != null){
                    System.out.println("Odebrano: "+ message);
                    textArea.append(message +"\n");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

        //start klienta
    public static void main(String[] argv){
       J_Com_Client klient = new J_Com_Client();
        //utwórz okno


        String nickname =  JOptionPane.showInputDialog(null, "Wprowadź swój pseudonim:", "Wprowadzenie", JOptionPane.QUESTION_MESSAGE);
        if(nickname.equals(""))
            //new ChatClient("Anonymous");
            klient.createWorksheet("Anon");
        else
            klient.createWorksheet(nickname);
        //new ChatClient(nickname);

        //klient.createWorksheet();
    }
}
