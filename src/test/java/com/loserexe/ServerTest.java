package com.loserexe;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
    private Server server;
    
    @Before
    public void connectToServer() throws IOException{
        this.server = new Server("localhost", "LoserEXE", 760, 9876);
    }

    @Test
    public void getServerList() throws IOException {
        this.server.serverListPing();
    }

	@Test
    public void connect() throws IOException{
        try {
            this.server.login(true);    
        } catch(Exception e) { 
            System.out.println(e.getMessage());
        } 
    }

    @After
    public void closeConnection() throws IOException{
        this.server.closeConnection();
    }
}
