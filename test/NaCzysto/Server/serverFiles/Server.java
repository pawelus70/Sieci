package serverFiles;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Server {
    ArrayList<ClientHandle> clientHandles;
    int amountConnected = 0;
    int usersIndex = 1;
    // private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService timedExecutorPool =Executors.newScheduledThreadPool(1);
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    Interface anInterface = new Interface();

    public void ShareClientList(int index) {
        clientHandles.get(index).printMessageWriter.println("001" + clientHandles.size());
        clientHandles.get(index).printMessageWriter.flush();
        for (int i = 0; i < clientHandles.size(); i++) {
            clientHandles.get(index).printMessageWriter.println(clientHandles.get(i).name);
            clientHandles.get(index).printMessageWriter.flush();
            clientHandles.get(index).printMessageWriter.println(i);
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
            this.index =clientHandles.indexOf(this);
            try {
                while ((message = bufferedMessageReader.readLine()) != null) {
                    time = System.nanoTime();
                    //System.out.println(name + ", " + LocalTime.now().withNano(0) + ", " + message);
                    anInterface.wiadomosci.append(name + ", " + LocalTime.now().withNano(0) + ", " + message + "\n");
                    anInterface.wiadomosci.setCaretPosition(anInterface.wiadomosci.getDocument().getLength());
                    requestListener(message, clientHandles.indexOf(this));
                }
            } catch (IOException e) {
                this.isConnected = false;
                e.printStackTrace();
                onDisconnection(clientHandles.indexOf(this));
                clientHandles.remove(this);
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
            clientHandles.get(index).name = message.substring(3)+"#"+(index+1);
        } else if (message.startsWith("003")) {//send to everyone
            message = message.substring(3);
            for (int i = 0; i < clientHandles.size(); i++) {
                if (i == index) {
                    clientHandles.get(i).printMessageWriter.println("You: " + message);
                    clientHandles.get(i).printMessageWriter.flush();
                    continue;
                };
                clientHandles.get(i).printMessageWriter.println(clientHandles.get(index).name +": " + message);
                clientHandles.get(i).printMessageWriter.flush();
            }
        }else {
            clientHandles.get(index).printMessageWriter.println("Unrecognizable request");
            clientHandles.get(index).printMessageWriter.flush();

        }
    }

    public void run() {
        clientHandles = new ArrayList();   //client list
        anInterface.run();
        showConnected();

        try {
            ServerSocket serverSocket = new ServerSocket(49152);
            //System.out.println("Server online. Server version (1.0.1).\n");
            anInterface.wiadomosci.append("Server online. Server version (1.0.1).\n");

            while (true) {
                Thread.sleep(10);
                Socket clientSocket = serverSocket.accept(); //accept request

                //receive connection message
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                clientHandles.add(new ClientHandle(clientSocket, usersIndex)); //add client to database
                threadPool.execute(clientHandles.get(clientHandles.size() - 1)); //execute client thread
                clientHandles.get(clientHandles.size() - 1).name = bufferedReader.readLine()+"#"+clientHandles.get(clientHandles.size() - 1).index; //set client name
                usersIndex+=1;

                //System.out.println("Connected new Client has connected.\t" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress());
                anInterface.logii.append("Connected new Client has connected.\n" + LocalDate.now() + ", " + LocalTime.now().withNano(0) + " " + clientSocket.getInetAddress() + "\n");
                amountConnected +=1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisconnection(int index) {
        anInterface.logii.append("User has disconnected, " + LocalDate.now() + ", " + LocalTime.now().withNano(0) + ",  " + clientHandles.get(index).name +"@"+ clientHandles.get(index).index+"\n");
        amountConnected -= 1;

    }



    public void showConnected() {
        final Runnable caller = new Runnable() {
            @Override
            public void run() {
                anInterface.userss.setText("Users: " + (clientHandles.size()));
                for (int i = 0; i < clientHandles.size(); i++) {
                    anInterface.userss.append("\n" + clientHandles.get(i).name + "@" + i);
                }
            }
        };
        final ScheduledFuture<?> scheduledFuture = timedExecutorPool.scheduleAtFixedRate(caller, 1, 1, TimeUnit.SECONDS);
        timedExecutorPool.schedule(caller, 0, TimeUnit.SECONDS);
        }



    public static void main(String[] argv) {
        Server client = new Server();
        client.run();
    }
}