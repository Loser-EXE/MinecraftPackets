package com.loserexe.protocol;

import com.google.gson.Gson;
import com.loserexe.Server;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.pojo.serverlist.ServerListJson;
import com.loserexe.utils.VarInt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerList {
    private int ping;
    private ServerListJson serverListJson;
    private String rawServerListJson;

    public ServerList (Server server) throws IOException {
        DataInputStream dataInputStream = server.getInputStream();
        DataOutputStream dataOutputStream = server.getOutputStream();
        Gson gson = new Gson();

        try {
            byte[] handshakeMessage = Handshake.getHandshakePacket(
                server.getPort(),
                server.getServerAddress(),
                server.getProtocolVersion(),
                1);

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

            this.rawServerListJson = json;

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

            this.serverListJson = gson.fromJson(json, ServerListJson.class);
            
        } catch (Exception e) {
            server.closeConnection();
            throw new IOException(e.getMessage());
        }
    }

    public int getPing() {
        return this.ping;
    }

    public ServerListJson getServerListJson() {
        return serverListJson;
    }

    public String getRawServerListJson() {
        return rawServerListJson; 
    }
}
