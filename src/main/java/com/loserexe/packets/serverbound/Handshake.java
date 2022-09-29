package com.loserexe.packets.serverbound;

import com.loserexe.utils.VarInt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Handshake {
    private static final Logger logger = LogManager.getLogger(Handshake.class.getName());
    private static final int PACKET_ID = 0x00;

    public static byte [] getHandshakePacket(int serverPort, String serverAddress, int protocolVersion, int nextState) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(buffer);

        handshake.writeByte(PACKET_ID);
        logger.info("Wrote to packet: ID = " + PACKET_ID);
        VarInt.write(handshake, protocolVersion);
        logger.info("Wrote to packet: ProtocolVersion = " + protocolVersion);
        VarInt.write(handshake, serverAddress.length());
        logger.info("Wrote to packet: ServerAddressLength = " + serverAddress.length());
        handshake.writeBytes(serverAddress);
        logger.info("Wrote to packet: serverAddress = " + serverAddress);
        handshake.writeShort((short) serverPort);
        logger.info("Wrote to packet: serverPort = " + serverPort);
        VarInt.write(handshake, nextState);
        logger.info("Wrote to packet: nextState = " + nextState);

        return buffer.toByteArray();
    }
}
