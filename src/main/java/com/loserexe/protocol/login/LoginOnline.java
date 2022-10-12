package com.loserexe.protocol.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.loserexe.Server;
import com.loserexe.auth.PlayerAuth;
import com.loserexe.packets.clientbound.EncryptionRequest;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.packets.serverbound.login.LoginStart;
import com.loserexe.pojo.minecraft.PlayerCertificatesJson;
import com.loserexe.utils.VarInt;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.*;

public class LoginOnline {
		private static final Logger logger = LogManager.getLogger(LoginOnline.class.getName());


		public static void login(Server server) throws IOException, InterruptedException, NoSuchAlgorithmException {
				logger.info("Started LoginOnline protocol...");
				DataOutputStream dataOutputStream = server.getOutputStream();
                DataInputStream dataInputStream = server.getInputStream();

				PlayerAuth playerAuth = new PlayerAuth();

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

				PlayerCertificatesJson playerCertificates = playerAuth.getPlayerCertificates();
			
				logger.info("Creating LoginOnline packet...");
				byte[] login = LoginStart.getLoginStartPacket(server.getUsername(), playerAuth.getPlayerProfileJson(), playerCertificates);
				VarInt.write(dataOutputStream, login.length);
				logger.debug("Wrote to dataOutputSteam: LoginOnline.length = " + login.length);

				dataOutputStream.write(login);
				logger.info("Sent LoginOnline packet");

                logger.info("Attempting to read server encryptionRequest...");
                EncryptionRequest encryptionRequest = new EncryptionRequest(dataInputStream);

                byte[] sharedSecret = new byte[16];
                Random random = new Random();
                random.nextBytes(sharedSecret);
                logger.info("Generated sharedSecret: " + Hex.encodeHexString(sharedSecret));

                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                messageDigest.update(encryptionRequest.getServerID().getBytes());
                messageDigest.update(sharedSecret);
                messageDigest.update(encryptionRequest.getPublicKey());
                String hash = new BigInteger(messageDigest.digest()).toString(16);
                logger.info("Generated sha1 hash: " + hash);

                playerAuth.authClient(hash);
		}
}
