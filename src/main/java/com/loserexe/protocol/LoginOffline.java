package com.loserexe.protocol;

import com.loserexe.Server;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.packets.serverbound.login.LoginOfflineStart;
import com.loserexe.utils.VarInt;

import java.io.DataOutputStream;
import java.io.IOException;

public class LoginOffline {
    public static void login(Server server) throws IOException {
        DataOutputStream dataOutputStream = server.getOutputStream();

        byte[] handshakeMessage = Handshake.getHandshakePacket(
                server.getPort(),
                server.getServerAddress(),
                server.getProtocolVersion(),
                2);

        VarInt.write(dataOutputStream, handshakeMessage.length);
        dataOutputStream.write(handshakeMessage);

        byte[] login = LoginOfflineStart.getLoginStartPacket(server.getUsername());
        VarInt.write(dataOutputStream, login.length);
        dataOutputStream.write(login);
    }
}
