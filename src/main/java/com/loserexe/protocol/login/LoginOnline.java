package com.loserexe.protocol.login;

import java.io.DataOutputStream;
import java.io.IOException;

import com.loserexe.Server;
import com.loserexe.auth.PlayerAuth;
import com.loserexe.packets.serverbound.Handshake;
import com.loserexe.packets.serverbound.login.LoginStart;
import com.loserexe.pojo.minecraft.PlayerCertificatesJson;
import com.loserexe.utils.VarInt;

import org.apache.logging.log4j.*;

public class LoginOnline {
		private static final Logger logger = LogManager.getLogger(LoginOnline.class.getName());


		public static void login(Server server) throws IOException, InterruptedException {
				logger.info("Started LoginOnline protocol...");
				DataOutputStream dataOutputStream = server.getOutputStream();

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
		}
}
