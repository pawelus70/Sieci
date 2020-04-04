

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;


public class J_Com_Client {
    JTextArea textArea; //pole odebranych wiadomości
    JTextField messageField; //pole wiadomości
    BufferedReader buffer;
    PrintWriter writer; //"pisarz" wiadomości
    Socket clientSocket; //adres i port serwera


    public static void main(String[] argv){
        J_Com_Client klient = new J_Com_Client();
        klient.createWorksheet();
    }

    public  void createWorksheet(){
        JFrame border = new JFrame("Klient");
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
        border.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void clientStart(){
        try {
            InetAddress host = InetAddress.getLocalHost();
            clientSocket = new Socket("127.0.0.1",4242); //Adres IP hosta w tym przypadku 192.168.8.106 dla testw między urządzeniami. Do testów zastąpić 127.0.0.1
            InputStreamReader serwerReader = new InputStreamReader(clientSocket.getInputStream());
            buffer = new BufferedReader(serwerReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("gotowe do użycia");
            textArea.append("Połączono jako: " + host.getHostAddress() + "\n"); //Wiadomość o połączeniu
       }catch(IOException e){
            e.printStackTrace();
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
