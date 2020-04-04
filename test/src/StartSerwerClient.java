//USSLESS ATM

import TestClient.TestClient;
import TestServer.TestServer;

public class StartSerwerClient {
    public static void main(String[] argv){
        TestServer serwer = new TestServer();
        serwer.serverSetup();

        TestClient klient = new TestClient();
        klient.createWorksheet();

    }
}
