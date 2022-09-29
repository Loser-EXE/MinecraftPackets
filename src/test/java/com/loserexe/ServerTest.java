package com.loserexe;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {
    private Server server;

    @BeforeEach
    public void connectToServer() throws IOException{
        this.server = new Server("mc.loserexe.com", "Bruh", 760, 25565);
    }

    @Test
    public void getServerList() throws IOException {
        this.server.serverListPing(760);
        System.out.println(this.server.getServerList().getRawServerListJson());
    }

    @Test
    public void connectPlayer() throws IOException{
        this.server.offlineLogin();
        this.server.getInputStream().read();
    }

    @AfterEach
    void closeConnection() throws IOException{
        this.server.closeConnection();
    }
}
