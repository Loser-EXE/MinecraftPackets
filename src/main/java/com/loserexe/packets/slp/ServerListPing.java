package com.loserexe.packets.slp;

import com.loserexe.packets.slp.serverbound.Handshake;
import com.loserexe.utils.VarInt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerListPing {
    private String serverAddress;
    private int port;
    private int protocolVersion;
    private final int TIMEOUT = 7000;

    private int ping;

    public ServerListPing(String serverAddress, int port, int protocolVersion) throws IOException {
        this.serverAddress = serverAddress;
        this.port = port;
        this.protocolVersion = protocolVersion;

        InetSocketAddress host = new InetSocketAddress(this.serverAddress, this.port);
        Socket socket = new Socket();
        socket.connect(host, TIMEOUT);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        byte[] handshakeMessage = Handshake.getHandshakePacket(this.port, this.serverAddress, this.protocolVersion);

        VarInt.write(dataOutputStream, handshakeMessage.length);
        dataOutputStream.write(handshakeMessage);

        dataOutputStream.writeByte(0x01); // Status Request
        dataOutputStream.writeByte(0x00); // Ping Request

        VarInt.read(dataInputStream);
        int id = VarInt.read(dataInputStream);

        if (id == -1) throw new IOException("Premature end of stream.");
        if (id != 0x00) throw new IOException("Invalid PacketID.");

        int length = VarInt.read(dataInputStream);

        if (length == -1) throw new IOException("Premature end of stream.");
        if (length == 0) throw new IOException("Invalid string length"); //That's what she said

        byte[] input = new byte[length];
        dataInputStream.readFully(input);
        String json = new String(input);

        long now = System.currentTimeMillis();

        dataOutputStream.writeByte(0x09);
        dataOutputStream.writeByte(0x01);
        dataOutputStream.writeLong(now); // Ping

        VarInt.read(dataInputStream);
        id = VarInt.read(dataInputStream);

        if (id == -1) throw new IOException("Premature end of stream.");
        if (id != 0x01) throw new IOException("Invalid PacketID.");

        dataInputStream.readLong(); // Pong
        long pingTime = System.currentTimeMillis();

        this.ping = (int) (pingTime - now); // Not very accurate

        System.out.println(json);
        System.out.println(this.ping);

        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }

    public int getPing() {
        return this.ping;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }
}
