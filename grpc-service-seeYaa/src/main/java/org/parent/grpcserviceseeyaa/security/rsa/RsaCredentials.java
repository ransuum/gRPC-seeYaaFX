package org.parent.grpcserviceseeyaa.security.rsa;

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

@Component
public class RsaCredentials {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RsaCredentials(RSAKeyRecord props) {
        this.publicKey = readPublicKey(props.rsaPublicKey());
        this.privateKey = readPrivateKey(props.rsaPrivateKey());
    }

    public String encrypt(String email, String password) {
        try {
            String credentials = email + ':' + password;
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(credentials.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot encrypt credentials", e);
        }
    }

    /**
     * returns [email,password]
     */
    public String[] decrypt(String base64) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String creds = new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
            return creds.split(":", 2);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot decrypt credentials", e);
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
