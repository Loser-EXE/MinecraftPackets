package com.loserexe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loserexe.protocol.Login;
import com.loserexe.protocol.ServerList;
import com.loserexe.utils.Favicon;

public class Server {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final int TIMEOUT = 7000;
    private final String username;
    private final int port;
    private final int protocolVersion;
    private String serverAddress;
    private ServerList serverList;
    private InetSocketAddress host;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Server(String serverAddress, String username, int protocolVersion, int port) throws IOException{
        logger.info("Creating server...");
        this.serverAddress = serverAddress;
        this.username = username;
        this.port = port;
        this.protocolVersion = protocolVersion;
        connectToServer();
    } 

    public Server(String serverAddress, String username) throws IOException{
        this.serverAddress = serverAddress;
        this.username = username;
        this.port = 25565;
        this.protocolVersion = -1;
        connectToServer();
    }

    private void connectToServer() throws IOException{
        logger.info("Connecting to server (addr=" + this.serverAddress + ", port=" + this.port + ')');
        try {
            this.host = new InetSocketAddress(this.serverAddress, this.port);
            this.socket = new Socket();
            this.socket.connect(this.host, this.TIMEOUT);
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        } catch(Exception e) {
            logger.fatal("Failed to connect to server: " + e.getMessage(), e);
            throw e;
        }
    }

    public void serverListPing() throws IOException{
        this.serverList = new ServerList(this);
    }

	public void login(boolean isOnline) throws IOException, InterruptedException, NoSuchAlgorithmException {
        if (isOnline) {
            Login.LoginOnline(this);
        } else {
            Login.LoginOffline(this);
        }
	}

    public void closeConnection() throws IOException {
        try {
            this.dataOutputStream.close();
            this.dataInputStream.close();
            this.socket.close();
            logger.info("Closed connection to server " + this.serverAddress);
        } catch(Exception e) {
            logger.error("Cant close connection to server: " + e.getMessage(), e);
        }
    }

    public void favicon(String path) throws IOException{
        try {
            Favicon.faviconToPng(this.getServerList().getServerListJson().getFavicon(), path);
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }

    public ServerList getServerList() {
        return this.serverList;
    }

    public DataOutputStream getOutputStream() {
        return this.dataOutputStream;
    }

    public DataInputStream getInputStream() {
        return this.dataInputStream;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverAddress='" + this.serverAddress + '\'' +
                ", port='" + this.port + "\'" + 
                ", ping='" + this.serverList.getPing() + '\'' +
                '}'; 
    }
}
