package op.tools;

import entity.system.SYSProps;
import entity.system.SYSPropsTools;
import op.OPDE;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

/**
 * http://www.exampledepot.com/egs/javax.crypto/PassKey.html
 */
public class DesEncrypter {
    Cipher ecipher;
    Cipher dcipher;

    // Iteration count
    int iterationCount = 19;



    public DesEncrypter(String passPhrase) {
        try {

            // i am doing this to reproduce same keys on the same machine. especially a mac tends to shuffle the nic list from time to time
            NetworkInterface ni = NetworkInterface.getByName(OPDE.getLocalProps().getProperty(SYSPropsTools.LOCAL_KEY_CIPHER_NIC, ""));
            if (ni == null) {
                ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                if (ni == null) { // Das ist nötig, weil ein Linux in einer VMWare hier ein NULL liefert.
                    ni = NetworkInterface.getNetworkInterfaces().nextElement();
                }
                OPDE.getLocalProps().setProperty(SYSPropsTools.LOCAL_KEY_CIPHER_NIC, ni.getName());
                OPDE.saveLocalProps();
            }

            // Die 6-Bytes MAC Adresse muss noch um zwei weitere, beliebige Bytes aufgefüllt werden. Das verlangt der Algorithmus
            byte[] salt = ArrayUtils.addAll(ni.getHardwareAddress(), new byte[]{(byte) 0x9B, (byte) 0xC8});

            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String str) {
        try {

            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String str) throws Exception {

        // Decode base64 to get bytes
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

        // Decrypt
        byte[] utf8 = dcipher.doFinal(dec);

        // Decode using utf-8
        return new String(utf8, "UTF8");
    }
}