package com.example.encryptednotes.activities;
import static com.example.encryptednotes.shared.Keys.HEADER_KEY;
import static com.example.encryptednotes.shared.Keys.NOTE_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.encryptednotes.R;
import com.example.encryptednotes.shared.EncryptedSharedPreference;
import com.google.android.material.button.MaterialButton;


public class NotesActivity extends AppCompatActivity {

    private static final int REQUEST_PWD_PROMPT = 1;
    private EditText mNoteEditText;
    private EditText mNoteHeaderEditText;
    private MaterialButton mSaveButton;
    private MaterialButton mDeleteButton;
    private EncryptedSharedPreference mEncryptedSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initView();
        setListeners();

        try {
            mEncryptedSharedPreference = new EncryptedSharedPreference(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // see if this is being called from our password request..?
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PWD_PROMPT) {
            // ..it is. Did the user get the password right?
            if (resultCode == RESULT_OK) {
                // they got it right
            } else {
                // they got it wrong/cancelled
            }
        }
    }

    private void setListeners() {
        mSaveButton.setOnClickListener(e -> saveNote());
        mDeleteButton.setOnClickListener(e -> deleteNote());
    }

    private void deleteNote() {
        String header = mNoteHeaderEditText.getText().toString();
        mEncryptedSharedPreference.delete(header);
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    private void saveNote() {
        String header = mNoteHeaderEditText.getText().toString();
        String note = mNoteEditText.getText().toString();
        if (header.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a header for the note", Toast.LENGTH_SHORT).show();
            return;
        }
        mEncryptedSharedPreference.putNote(header, note);
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mNoteEditText = findViewById(R.id.note_edit_text);
        mNoteHeaderEditText = findViewById(R.id.note_header_edit_text);
        mSaveButton = findViewById(R.id.save_button);
        mDeleteButton = findViewById(R.id.delete_button);
        String header = getIntent().getExtras().getString(HEADER_KEY);
        String content = getIntent().getExtras().getString(NOTE_KEY);
        if (header != null)
            mNoteHeaderEditText.setText(header);
        if (content != null)
            mNoteEditText.setText(content);
    }
}
