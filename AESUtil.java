import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    // 16-character key (AES-128)
    private static final String SECRET_KEY = "1234567890123456";

    // Encrypt Method
    public static String encrypt(String message) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(
                SECRET_KEY.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(
                message.getBytes("UTF-8"));

        return Base64.getEncoder()
                .encodeToString(encryptedBytes);
    }

    // Decrypt Method
    public static String decrypt(String encryptedMessage) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(
                SECRET_KEY.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedBytes = Base64.getDecoder()
                .decode(encryptedMessage);

        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, "UTF-8");
    }

}
