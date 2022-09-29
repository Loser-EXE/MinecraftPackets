package com.loserexe.protocol;

import com.google.gson.Gson;
import com.loserexe.Server;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.pojo.serverlist.ServerListJson;
import com.loserexe.utils.VarInt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
    private Logger logger = LogManager.getLogger(this.getClass().getName());
    private int ping;
    private ServerListJson serverListJson;
    private String rawServerListJson;

    public ServerList (Server server) throws IOException {
        logger.info("Started ServerList protcol...");
        DataInputStream dataInputStream = server.getInputStream();
        DataOutputStream dataOutputStream = server.getOutputStream();
        Gson gson = new Gson();

        try {
            logger.info("Creating handshake packet...");
            byte[] handshakeMessage = Handshake.getHandshakePacket(
                server.getPort(),
                server.getServerAddress(),
                server.getProtocolVersion(),
                1);
            logger.info("Wrote to dataOutputSteam: handshakeMessageLength = " + handshakeMessage.length);
            VarInt.write(dataOutputStream, handshakeMessage.length);
            logger.info("Sent handshakeMessage packet");
            dataOutputStream.write(handshakeMessage);

            dataOutputStream.writeByte(0x01); // Status Request
            dataOutputStream.writeByte(0x00); // Ping Request

            VarInt.read(dataInputStream);

            int id = VarInt.read(dataInputStream);

            if (id == -1) throw new IOException("Premature end of stream.");
            if (id != 0x00) throw new IOException("Invalid PacketID " + id);

            int length = VarInt.read(dataInputStream);

            if (length == -1) throw new IOException("Premature end of stream.");
            if (length == 0) throw new IOException("Invalid string length" + length); //That's what she said

            byte[] input = new byte[length];
            dataInputStream.readFully(input);
            String json = new String(input);
            logger.info("Recevied json data from server " + json);

            this.rawServerListJson = json;

            long now = System.currentTimeMillis();

            dataOutputStream.writeByte(0x09);
            dataOutputStream.writeByte(0x01);
            dataOutputStream.writeLong(now); // Ping

            VarInt.read(dataInputStream);
            id = VarInt.read(dataInputStream);

            if (id == -1) throw new IOException("Premature end of stream.");
            if (id != 0x01) throw new IOException("Invalid PacketID " + id);

            dataInputStream.readLong(); // Pong
            long pingTime = System.currentTimeMillis();

            this.ping = (int) (pingTime - now); // Not very accurate

            this.serverListJson = gson.fromJson(json, ServerListJson.class);
            
        } catch (Exception e) {
            server.closeConnection();
            logger.fatal("Error getting server list: " + e.getMessage());
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
