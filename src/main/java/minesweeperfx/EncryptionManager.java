package minesweeperfx;

import java.io.*;
import javax.crypto.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptionManager {

    private static final String PASSWORD = "7sfD/KsrH?e6p).[?!(*@dv&>`4Ab%{_";

    public static void encryptionFile(File file, Object data) throws IOException {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

        SealedObject sealedObject;
        
        if(data instanceof Serializable) {
            try {
                sealedObject = new SealedObject((Serializable) data, cipher);
        
                CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(file)), cipher);

                try (ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream)) {
                    outputStream.writeObject(sealedObject);
                }
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(EncryptionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new NotSerializableException();
        }
    }

    public static Object decryptObject(File file, Class classType) throws IOException, ClassNotFoundException {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

        CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(file)), cipher);
        ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
        SealedObject sealedObject = (SealedObject) inputStream.readObject();

        Object data;

        try {
            data = sealedObject.getObject(cipher);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            return null;
        }

        return data.getClass().equals(classType) ? data : null;
    }

    private static Cipher getCipher(int cipherMode) {
//        byte[] keyBytes = encryptionPassword.getBytes();
//        DESKeySpec desKeySpec;
//        Cipher desCipher = null;
//        
//        try {
//            desKeySpec = new DESKeySpec(keyBytes);
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//
//            // Create Cipher
//            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//            desCipher.init(cipherMode, secretKey);
//        } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
//            Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }

        Cipher cipher = null;

        try {
            byte[] keyBytes = PASSWORD.getBytes();
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, key);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(MinesweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cipher;
    }
}
