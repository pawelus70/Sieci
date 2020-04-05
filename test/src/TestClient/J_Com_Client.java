

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class J_Com_Client  {
    JTextArea textArea; //pole odebranych wiadomości
    JTextField messageField; //pole wiadomości
    BufferedReader buffer;
    PrintWriter writer; //"pisarz" wiadomości
    Socket clientSocket; //adres i port serwera
    private String name;

    public static void main(String[] argv){
        J_Com_Client klient = new J_Com_Client();

        String nickname =  JOptionPane.showInputDialog(null, "Wprowadź swój pseudonim:", "Wprowadzenie", JOptionPane.PLAIN_MESSAGE);
        if(nickname.equals(""))
            //new ChatClient("Anonymous");
            klient.createWorksheet("Anon");
        else
            klient.createWorksheet(nickname);
            //new ChatClient(nickname);

        //klient.createWorksheet();
    }

    public  void createWorksheet(String n){
        name = n;
        this.clientSocket = clientSocket;

        JFrame border = new JFrame("Klient"+name);
        JPanel mainInterface = new JPanel();

        textArea = new JTextArea(15,60);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        messageField = new JTextField(20);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        messageField.addActionListener(new SendButtonListener());//wyślij enter

        mainInterface.add(scrollPane);
        mainInterface.add(messageField);
        mainInterface.add(sendButton);

        clientStart();

        Thread clientThread = new Thread(new warningsReciver());
        clientThread.start();

        border.getContentPane().add(BorderLayout.CENTER,mainInterface);
        border.setSize(720,600);
        border.setVisible(true);
        border.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        border.addWindowListener(new WindowAdapter() {// na zamknięcie okna
            @Override
            public void windowClosing(WindowEvent arg0){ //nasłuchuj zamknięcia
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
    }

    public void clientStart(){
        try {
            InetAddress host = InetAddress.getLocalHost();
            clientSocket = new Socket("127.0.0.1",4242); //Adres IP hosta w tym przypadku 192.168.8.106 dla testw między urządzeniami. Do testów zastąpić 127.0.0.1
            InputStreamReader serwerReader = new InputStreamReader(clientSocket.getInputStream());
            buffer = new BufferedReader(serwerReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("gotowe do użycia");
            textArea.append("Połączono jako: " + name + "\n"); //Wiadomość o połączeniu
       }catch(IOException e){
            System.out.println(e); //błąd wyrzuc
            JOptionPane.showMessageDialog(null, "Nie połączono z serwerem."); // powiadomienie o niepowodzeniu
            System.exit(0); //zakończ
            //e.printStackTrace(); <Narazie wyrzucone>
        }
    }

    public class SendButtonListener implements ActionListener{
        LocalTime time = LocalTime.now();//pobierz czas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);//formatuj czas
        String czas = formatter.format(time);//przypisz czas do string

        public void actionPerformed(ActionEvent ev){
            try{
                InetAddress host = InetAddress.getLocalHost();
                writer.println("("+ czas + ") "+name + ": " + messageField.getText());
                writer.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            messageField.setText("");
            messageField.requestFocus();
        }
    }

    public class warningsReciver implements Runnable{

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
    }
