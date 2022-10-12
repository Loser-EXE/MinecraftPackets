package com.loserexe.packets.clientbound;

import java.io.DataInputStream;
import java.io.IOException;

import com.loserexe.utils.VarInt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptionRequest {
    private Logger logger = LogManager.getLogger(this.getClass().getName());
    private String serverID;
    private int publicKeyLength;
    private byte[] publicKey;
    private int verifyTokenLength;
    private byte[] verifyToken;

    public EncryptionRequest (DataInputStream inputStream) throws IOException {
        inputStream.read();

        int packetID = inputStream.read();

        if (packetID != 0x01) {
            logger.warn("Invalid packet id: " + packetID);
            throw new IOException("Invalid packet id expeted 0x01 got: " + packetID);
        }

        int serverIDLength = VarInt.read(inputStream);
        byte[] serverIDBytes = new byte[serverIDLength];
        logger.info("Read from server: serverIDLength = " + serverIDLength);
        inputStream.readFully(serverIDBytes);
        serverID = new String(serverIDBytes);
        logger.info("Read from server: serverID = " + serverID);

        publicKeyLength = VarInt.read(inputStream);
        logger.info("Read from server: publicKeyLength = " + publicKeyLength);
        publicKey = new byte[publicKeyLength]; 
        inputStream.readFully(publicKey);
        logger.info("Read from server: publicKey");

        verifyTokenLength = VarInt.read(inputStream);
        logger.info("Read from server: verifyTokenLength = " + verifyTokenLength);
        verifyToken = new byte[verifyTokenLength];
        inputStream.readFully(verifyToken);
        logger.info("Read from server: verifyToken");

        logger.info("Finished reading packet");
    }

    public String getServerID() {
        return this.serverID;
    }

    public int getPublicKeyLength() {
        return this.publicKeyLength;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public int getVerifyTokenLength() {
        return this.verifyTokenLength;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
