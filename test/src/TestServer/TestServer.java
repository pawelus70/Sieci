package TestServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class TestServer {

    ArrayList inputStream;

    public class clientHandle implements Runnable {
        BufferedReader buffer;
        Socket clientSocket;

        public clientHandle(Socket clientSocketHandle){
            try{
                clientSocket = clientSocketHandle;
                InputStreamReader isReader=new InputStreamReader((clientSocket.getInputStream()));
                buffer = new BufferedReader(isReader);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try{
                while((message = buffer.readLine())!= null){
                    System.out.println("Odczytano: "+message);
                    sendToEveryone(message);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] argv){
        TestServer server = new TestServer();
        server.serverSetup();
    }

    public void serverSetup(){
        inputStream = new ArrayList();
        try{
            ServerSocket serverSocket = new ServerSocket(4242);
             while(true){
                 Socket clientSocet1=serverSocket.accept();
                 PrintWriter writer = new PrintWriter(clientSocet1.getOutputStream());
                 inputStream.add(writer);

                 Thread thread = new Thread(new clientHandle(clientSocet1));
                 thread.start();
                 System.out.println("Połączono");
             }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void sendToEveryone(String message){
        Iterator it = inputStream.iterator();
        while(it.hasNext()){
            try{
                PrintWriter printWriter =(PrintWriter)it.next();
                printWriter.println(message);
                printWriter.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
