package org.parent.grpcserviceseeyaa.security.rsa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Arrays;

@Component
public class RsaCredentials {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private static final int MAX_CREDENTIAL_LENGTH = 512; // Prevent DoS

    public RsaCredentials(RSAKeyRecord props) {
        this.publicKey = readPublicKey(props.rsaPublicKey());
        this.privateKey = readPrivateKey(props.rsaPrivateKey());
    }

    public String encrypt(String email, String password) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }
        String credentials = email + ':' + password;
        if (credentials.length() > MAX_CREDENTIAL_LENGTH) {
            throw new IllegalArgumentException("Credentials exceed maximum length");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(credentials.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public String[] decrypt(String base64) {
        if (StringUtils.isBlank(base64)) {
            throw new IllegalArgumentException("Encrypted credentials cannot be null or empty");
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encrypted);
            String creds = new String(decryptedBytes, StandardCharsets.UTF_8);
            Arrays.fill(decryptedBytes, (byte) 0);
            String[] parts = creds.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalStateException("Invalid credential format");
            }
            return parts;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid base64 encoding", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private static RSAPublicKey readPublicKey(Resource res) {
        try (InputStream in = res.getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] data = Base64.getDecoder().decode(pem);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(data));
        } catch (IOException | GeneralSecurityException ex) {
            throw new IllegalStateException("Cannot read RSA public key", ex);
        }
    }

    private static RSAPrivateKey readPrivateKey(Resource res) {
        try (InputStream in = res.getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] data = Base64.getDecoder().decode(pem);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(data));
        } catch (IOException | GeneralSecurityException ex) {
            throw new IllegalStateException("Cannot read RSA private key", ex);
        }
    }
}
