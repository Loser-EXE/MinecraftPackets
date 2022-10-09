package com.loserexe.pojo.minecraft;

public class PlayerCertificatesJson {
		private KeyPair keyPair;
		private String publicKeySignature;
		private String publicKeySignatureV2;
		private String expiresAt;
		private String refreshedAfter;

		public KeyPair getKeyPair() {
				return this.keyPair;
		}

		public String getPublicKeySignature() {
				return this.publicKeySignature;
		}

		public String getPublicKeySignatureV2() {
				return this.publicKeySignatureV2;
		}

		public String getExpiresAt() {
				return this.expiresAt;
		}

		public String getRefreshedAfter() {
				return this.refreshedAfter;
		}
}
