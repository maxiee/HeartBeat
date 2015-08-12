package com.maxiee.heartbeat.backup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by maxiee on 15-8-12.
 */
public class FileDES {
    private final static String KEY_RULE = "maxieejudy";

    private static Key initKey() {
        byte[] keyByte = KEY_RULE.getBytes();
        byte[] byteTemp = new byte[8];
        for (int i=0; i<byteTemp.length && i<keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        return new SecretKeySpec(byteTemp, "DES");
    }

    public static void doEncryptFile(File in, File out) {
        if (in == null) return;
        try {
            Cipher encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, initKey());
            FileInputStream is = new FileInputStream(in);
            CipherInputStream cin = new CipherInputStream(is, encryptCipher);
            OutputStream os = new FileOutputStream(out);
            byte[] buffer = new byte[1024];
            int len;
            while ((len=cin.read(buffer)) > 0) {
                os.write(buffer, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            is.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static boolean doDecryptFile (InputStream in, OutputStream os) throws Exception{
        if (in == null) return false;
        Cipher decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, initKey());
        CipherInputStream cin = new CipherInputStream(in, decryptCipher);
        byte[] buffer = new byte[1024];
        int len;
        while ((len=cin.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        cin.close();
        in.close();
        os.close();
        return true;
    }
}
