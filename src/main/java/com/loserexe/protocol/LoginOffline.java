package com.loserexe.protocol;

import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.packets.serverbound.login.LoginOfflineStart;
import com.loserexe.utils.VarInt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LoginOffline {
    private String serverAddress;
    private int port;
    private int protocolVersion;

    private final int TIMEOUT = 7000;

    public LoginOffline(String serverAddress, int port, int protocolVersion, String name) throws IOException {
        this.serverAddress = serverAddress;
        this.port = port;
        this.protocolVersion = protocolVersion;

        InetSocketAddress host = new InetSocketAddress(this.serverAddress, this.port);
        Socket socket = new Socket();
        socket.connect(host, TIMEOUT);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        byte[] handshakeMessage = Handshake.getHandshakePacket(
                this.port,
                this.serverAddress,
                this.protocolVersion,
                2);

        VarInt.write(dataOutputStream, handshakeMessage.length);
        dataOutputStream.write(handshakeMessage);

        byte[] login = LoginOfflineStart.getLoginStartPacket(name);
        VarInt.write(dataOutputStream, login.length);
        System.out.println(login.length);
        dataOutputStream.write(login);

        dataInputStream.read();

        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }
}
