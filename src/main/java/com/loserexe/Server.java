package com.loserexe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.loserexe.protocol.LoginOffline;
import com.loserexe.protocol.ServerList;
import com.loserexe.utils.Favicon;

public class Server {
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
        this.serverAddress = serverAddress;
        this.username = username;
        this.port = port;
        this.protocolVersion = protocolVersion;
        connectToServer();
    } 

    public Server(String serverAddress, String username) throws IOException{
        connectToServer();
        this.serverAddress = serverAddress;
        this.username = username;
        this.port = 25565;
        this.protocolVersion = -1;
    }

    private void connectToServer() throws IOException{
        this.host = new InetSocketAddress(this.serverAddress, this.port);
        this.socket = new Socket();
        this.socket.connect(this.host, this.TIMEOUT);
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void serverListPing(int protocolVersion) throws IOException{
        this.serverList = new ServerList(this);
    }

    public void offlineLogin() throws IOException{
        LoginOffline.login(this);
    }

    public void closeConnection() throws IOException{
        this.dataOutputStream.close();
        this.dataInputStream.close();
        this.socket.close();
    }

    public void favicon(String path) throws IOException{
        Favicon.faviconToPng(this.getServerList().getServerListJson().getFavicon(), path);
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