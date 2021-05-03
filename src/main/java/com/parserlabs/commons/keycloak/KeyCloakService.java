package com.parserlabs.commons.keycloak;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Map;

import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("keyCloakService")
public class KeyCloakService {

	public AccessToken fetchAccessToken(String token, PublicKey publicKey) throws VerificationException {
		token = token.substring(7).trim();
		return RSATokenVerifier.create(token).checkActive(true).publicKey(publicKey).getToken();
	}

	@SuppressWarnings("unchecked")
	public PublicKey fetchPublicKey(String certUrl) throws Exception {
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> certInfos = om.readValue(new URL(certUrl).openStream(), Map.class);
		List<Map<String, Object>> keys = (List<Map<String, Object>>) certInfos.get("keys");
		Map<String, Object> keyInfo = null;

		keyInfo = keys.get(0);
		if (keyInfo == null) {
			return null;
		}

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		String modulusBase64 = (String) keyInfo.get("n");
		String exponentBase64 = (String) keyInfo.get("e");

		Decoder urlDecoder = java.util.Base64.getUrlDecoder();
		BigInteger modulus = new BigInteger(1, urlDecoder.decode(modulusBase64));
		BigInteger publicExponent = new BigInteger(1, urlDecoder.decode(exponentBase64));

		return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
	}
}
