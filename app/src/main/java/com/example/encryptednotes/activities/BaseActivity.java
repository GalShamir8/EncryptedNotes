package com.example.encryptednotes.activities;

import static com.example.encryptednotes.shared.Keys.HEADER_KEY;
import static com.example.encryptednotes.shared.Keys.NOTE_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.encryptednotes.R;
import com.example.encryptednotes.models.Note;
import com.example.encryptednotes.shared.EncryptedSharedPreference;
import com.example.encryptednotes.shared.NotesAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    private RecyclerView notesRecyclerView;
    private EncryptedSharedPreference mEncryptedSharedPreference;
    private ArrayList<Note> notes;
    private MaterialButton newNoteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        try {
            mEncryptedSharedPreference = new EncryptedSharedPreference(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadNotes();
        initViews();
    }

    private void loadNotes() {
        notes = mEncryptedSharedPreference.getAlNotes();
    }

    private void initViews() {
        notesRecyclerView = findViewById(R.id.notes_recycler_view);
        newNoteButton = findViewById(R.id.new_note_button);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notesRecyclerView.setLayoutManager(layoutManager);
        setListeners();
    }

    private void setListeners() {
        setAdapter();
        newNoteButton.setOnClickListener(e -> openNote(new Note()));
    }

    private void setAdapter() {
        notesRecyclerView.setAdapter(new NotesAdapter(notes, this::openNote));
    }

    private void openNote(Note note) {
        Intent intent = new Intent(BaseActivity.this, NotesActivity.class);
        intent.putExtra(HEADER_KEY, note.getHeader());
        intent.putExtra(NOTE_KEY, note.getContent());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        updateData();
        super.onResume();
    }

    private void updateData() {
        loadNotes();
        setAdapter();
    }
}