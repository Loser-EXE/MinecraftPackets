package com.loserexe.packets.serverbound;

import com.loserexe.utils.VarInt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Handshake {
    private static final int PACKET_ID = 0x00;

    public static byte [] getHandshakePacket(int serverPort, String serverAddress, int protocolVersion, int nextState) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(buffer);

        handshake.writeByte(PACKET_ID);
        VarInt.write(handshake, protocolVersion);
        VarInt.write(handshake, serverAddress.length());
        handshake.writeBytes(serverAddress);
        handshake.writeShort((short) serverPort);
        VarInt.write(handshake, nextState);

        return buffer.toByteArray();
    }
}
