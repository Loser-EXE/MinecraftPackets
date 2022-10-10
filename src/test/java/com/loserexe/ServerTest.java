package com.loserexe;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
    private Server server;
    
    @Before
    public void connectToServer() throws IOException{
        this.server = new Server("localhost", "Nigger", 760, 9876);
    }

    @Test
    public void getServerList() throws IOException {
        this.server.serverListPing();
    }

	@Test
    public void connectOffline() throws IOException{
        this.server.offlineLogin();
        this.server.getInputStream().read();
    }

	@Test
	public void connectOnline() throws IOException, InterruptedException {
		this.server.onlineLogin();
		this.server.getInputStream().read();
	}

    @After
    public void closeConnection() throws IOException{
        this.server.closeConnection();
    }
}
