/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author Ray
 */
public class SSHAEncrypt {

    // Set Encrypt Key
    private static String SecretKey = "Clippy";
    Base64 bs = new Base64();
    // Set Encrypt Methodology
    private static SSHAEncrypt inst = new SSHAEncrypt("SHA-1");
    private MessageDigest sha = null;

    public static SSHAEncrypt getInstance() {
        return inst;
    }

    public SSHAEncrypt(String alg) {
        try {
            sha = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SSHAEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Use defined key to encrypt target password
     * @param password
     * @return 
     */
    public String getSSHAPassword(String password) {
        return createDigest(SecretKey.getBytes(), password);
    }

    /**
     * get encrypted string
     * @param salt
     * @param entity
     * @return 
     */
    private String createDigest(byte[] salt, String entity) {
        sha.reset();
        sha.update(entity.getBytes());
        sha.update(salt);
        byte[] pwhash = sha.digest();
        return bs.encodeToString(concatenate(pwhash, salt));
    }

    /**
     * concatenate two byte arrays
     * @param l
     * @param r
     * @return 
     */
    private static byte[] concatenate(byte[] l, byte[] r) {
        byte[] b = new byte[l.length + r.length];
        System.arraycopy(l, 0, b, 0, l.length);
        System.arraycopy(r, 0, b, l.length, r.length);
        return b;
    }

    public boolean verifySHA(String storedpw, String inputpw)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        // decode BASE64
        byte[] storedpwbyte = bs.decode(storedpw);
        byte[] shacode;
        byte[] salt;

        // first 20 letters is SHA-1 encrypt part，all the others after 20 is random generated
        if (storedpwbyte.length <= 20) {
            shacode = storedpwbyte;
            salt = new byte[0];
        } else {
            shacode = new byte[20];
            salt = new byte[storedpwbyte.length - 20];
            System.arraycopy(storedpwbyte, 0, shacode, 0, 20);
            System.arraycopy(storedpwbyte, 20, salt, 0, salt.length);
        }

        // set user's password into message digest
        md.update(inputpw.getBytes());
        // set random generated string into message digest
        md.update(salt);

        // calculate user's password to SSHA encrypted string
        byte[] inputpwbyte = md.digest();

        return MessageDigest.isEqual(shacode, inputpwbyte);
    }

}
