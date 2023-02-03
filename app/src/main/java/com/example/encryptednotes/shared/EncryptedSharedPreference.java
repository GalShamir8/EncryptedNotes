package com.example.encryptednotes.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class EncryptedSharedPreference {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALIAS = "notes_key";

    private SharedPreferences mSharedPreferences;
    private KeyStore mKeyStore;

    public EncryptedSharedPreference(Context context) throws GeneralSecurityException, IOException {
        mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        mKeyStore.load(null);
        mSharedPreferences = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) throws GeneralSecurityException, IOException {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(KEY_ALIAS, null);
        Cipher inCipher = Cipher.getInstance(TRANSFORMATION);
        inCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inCipher);
        cipherOutputStream.write(value.getBytes("UTF-8"));
        cipherOutputStream.close();

        mSharedPreferences.edit().putString(key, Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)).apply();
    }

    public String getString(String key) throws GeneralSecurityException, IOException {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(KEY_ALIAS, null);
        Cipher outCipher = Cipher.getInstance(TRANSFORMATION);
        outCipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

        byte[] encryptedData = Base64.decode(mSharedPreferences.getString(key, ""), Base64.DEFAULT);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedData);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, outCipher);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }

        return new String(bytes, 0, bytes.length, "UTF-8");
    }
}

