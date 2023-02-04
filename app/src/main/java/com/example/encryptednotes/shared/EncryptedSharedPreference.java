package com.example.encryptednotes.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.encryptednotes.models.Note;

import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedSharedPreference {

    private static final String ALGORITHM = "AES";
    private static final String PREFERENCE_NAME = "encrypted_notes_pref";
    private static final String KEY_NAME = "encryption_key";
    private SharedPreferences mSharedPreferences;
    private Cipher mCipher;

    public EncryptedSharedPreference(Context context) throws Exception {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mCipher = Cipher.getInstance(ALGORITHM);
    }

    private Key generateKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(KEY_NAME.getBytes("UTF-8"));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public void putNote(String key, String value){
        try {
            mCipher.init(Cipher.ENCRYPT_MODE, generateKey());
            // Encrypt the value
            byte[] encryptedValue = mCipher.doFinal(value.getBytes());
            String encodedValue = Base64.encodeToString(encryptedValue, Base64.DEFAULT);

            // Save the encrypted value in the shared preference
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, encodedValue);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        try {
            mCipher.init(Cipher.DECRYPT_MODE, generateKey());
            // Get the encrypted value from the shared preference
            String encodedValue = mSharedPreferences.getString(key, null);

            // Decrypt the value
            byte[] encryptedValue = Base64.decode(encodedValue, Base64.DEFAULT);
            byte[] decryptedValue = mCipher.doFinal(encryptedValue);

            return new String(decryptedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Note> getAlNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        HashMap<String, String> data = (HashMap<String, String>) mSharedPreferences.getAll();
        for (Entry<String, String> entry: data.entrySet()){
            notes.add(buildNote(entry.getKey(), entry.getValue()));
        }
        return notes;
    }

    private Note buildNote(String header, String content) {
        return new Note(header, getString(header));
    }

    public void delete(String key){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}

