package com.loserexe.protocol;

import com.loserexe.Server;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.packets.serverbound.login.LoginOfflineStart;
import com.loserexe.utils.VarInt;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginOffline {
    private static final Logger logger = LogManager.getLogger(LoginOffline.class.getName());
    public static void login(Server server) throws IOException {
        logger.info("Started LoginOffline protocol...");
        DataOutputStream dataOutputStream = server.getOutputStream();

        logger.info("Creating handshake packet...");
        byte[] handshakeMessage = Handshake.getHandshakePacket(
                server.getPort(),
                server.getServerAddress(),
                server.getProtocolVersion(),
                2);

        VarInt.write(dataOutputStream, handshakeMessage.length);
        logger.debug("Wrote to dataOutputStream: handshakeMessage.length = " + handshakeMessage.length);
        dataOutputStream.write(handshakeMessage);
        logger.info("Sent handshakeMessage packet");

        logger.info("Creating LoginOffline packet...");
        byte[] login = LoginOfflineStart.getLoginStartPacket(server.getUsername());
        VarInt.write(dataOutputStream, login.length);
        logger.debug("Wrote to dataOutputStream: LoginOffline.length = " + login.length);
        dataOutputStream.write(login);
        logger.info("Sent LoginOffline packet");
    }
}
