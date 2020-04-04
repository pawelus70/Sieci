import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class J_Com_Client {
    JTextArea textArea; //pole odebranych wiadomości
    JTextField messageField; //pole wiadomości do wysłania
    BufferedReader buffer; //buffer
    PrintWriter writer; //"pisarz" wiadomości
    Socket clientSocket; //adres i port serwera

    public class clientData{

    }

    //utwórz okno
    public  void createWorksheet(){
        JFrame border = new JFrame("Klient"); //nazwa
        JPanel mainInterface = new JPanel();

        textArea = new JTextArea(15,60); //miejsce do wyświetlania tekstu
        textArea.setLineWrap(true); //zawijanie wierszy
        textArea.setWrapStyleWord(true); //zawijaj całe słowa
        textArea.setEditable(false);    //możliwość edycji tekstu w polu

        JScrollPane scrollPane = new JScrollPane(textArea); //scroll
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); //zawsze pionowo
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //nigdy poziomo

        messageField = new JTextField(45); //pole do wpisywania tekstu

        JButton sendButton = new JButton("Send"); //przycisk wyślij
        sendButton.addActionListener(new SendButtonListener()); //dodanie do niego akcji
        messageField.addActionListener(new SendButtonListener());//wyślij enter

        //dodanie elementów
        mainInterface.add(scrollPane);
        mainInterface.add(messageField);
        mainInterface.add(sendButton);


        //utwórz wątek dla klienta


        border.getContentPane().add(BorderLayout.CENTER,mainInterface);
        border.setSize(720,325);
        border.setVisible(true);
        border.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //setup klienta
    public void clientStart(){
        try {
           textArea.setText(null);
            InetAddress guest = InetAddress.getLocalHost();
            clientSocket = new Socket("127.0.0.1",4242); //Adres IP hosta w tym przypadku 192.168.8.106 dla testw między urządzeniami. Do testów zastąpić 127.0.0.1
            InputStreamReader serwerReader = new InputStreamReader(clientSocket.getInputStream());
            buffer = new BufferedReader(serwerReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("gotowe do użycia");
            textArea.append("Połączono jako: " + guest.getHostAddress() + "\n"); //Wiadomość o połączeniu
            Thread clientThread = new Thread(new warningsReceiver());
            clientThread.start();
       }catch(IOException e){
            textArea.append("Brak połączenia z serverem.\nPonawianie próby połączenia za 5 sekund.\n");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            clientStart();
        }
    }

    public class SendButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            try{
                InetAddress host = InetAddress.getLocalHost();
                writer.println(host.getHostAddress() + ": " + messageField.getText());
                writer.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            messageField.setText("");
            messageField.requestFocus();
        }
    }

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

        klient.createWorksheet();
        klient.clientStart();

    }
}
