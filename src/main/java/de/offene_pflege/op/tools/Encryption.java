package de.offene_pflege.op.tools;

import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by tloehr on 10.11.15.
 */
public class Encryption {


    //    private final String keyphrase;
    private final Key aesKey;
    private final Logger logger;

    public Encryption() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this(LocalMachine.getSerialNumber());
    }

    public Encryption(String keyphrase) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        logger = Logger.getLogger(getClass());

        byte[] k1 = keyphrase.getBytes();
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] k2 = sha.digest(k1);
        byte[] k3 = Arrays.copyOf(k2, 16); // use only first 128 bit
        this.aesKey = new SecretKeySpec(k3, "AES");

    }

    /**
     * encrypts a String using the generated LocalMachine.getSerialNumber()
     *
     * @return
     */
    public String encrypt(String secret) {
        byte[] crypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            crypted = cipher.doFinal(secret.getBytes());
//            logger.debug(secret);
//            logger.debug(Base64.getEncoder().encodeToString(crypted));
        } catch (Exception e) {
            // bugger!
            crypted = null;
        }


        return crypted != null ? Base64.getEncoder().encodeToString(crypted) : null;
    }

    /**
     * this is the opposite function of encrypt
     *
     * @param encrypted
     * @return
     */
    public String decrypt(String encrypted) {
        byte[] decrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
//            logger.debug(encrypted);
//            logger.debug(new String(decrypted));
        } catch (Exception e) {
            // bugger!
            decrypted = null;
        }


        return decrypted != null ? new String(decrypted) : null;
    }


//    public void encryptJDBCPassword(String password) throws UnsupportedEncodingException {
//        byte[] encrypted = encrypt(password.getBytes("UTF-8"));
//        OPDE.getLocalProps().put(SYSPropsTools.KEY_JDBC_PASSWORD, encrypted);
//    }

    public String decryptJDBCPasswort() {
        String jdbcpassword = "";
        try {
            jdbcpassword = SYSTools.catchNull(decrypt(SYSTools.catchNull(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_JDBC_PASSWORD))));
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }


        return jdbcpassword;
    }

}
