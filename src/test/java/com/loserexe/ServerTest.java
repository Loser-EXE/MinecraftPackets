package com.loserexe;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
    private Server server;
    
    @Before
    public void connectToServer() throws IOException{
        this.server = new Server("mc.loserexe.com", "Bruh", 760, 25565);
    }

    @Test
    public void getServerList() throws IOException {
        this.server.serverListPing(760);
    }

    @Test
    public void connectPlayer() throws IOException{
        this.server.offlineLogin();
        this.server.getInputStream().read();
    }

    @After
    public void closeConnection() throws IOException{
        this.server.closeConnection();
    }
}
