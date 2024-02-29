package com.example.firebase;

import static com.example.firebase.PCabActivity.notesList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {
    EditText et_title, et_description;
    Button buttonSave;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        buttonSave = findViewById(R.id.buttonSave);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString().trim();
                String description = et_description.getText().toString().trim();
                if (title.isEmpty() || description.isEmpty()){
                    Toast.makeText(AddNoteActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    int size = notesList.size();
                    int id = 1;
                    if (size != 0) id += size;
                    for (Note note: notesList) {
                        if (Objects.equals(note.getId(), String.valueOf(id))) id++;}
                    Note note = new Note(id, title, description);
                    db.collection("users").document(firebaseUser.getUid()).collection("notes").document(String.valueOf(id)).set(note).addOnSuccessListener(task -> {
                        Toast.makeText(AddNoteActivity.this, "Заметка успешно добавлена", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddNoteActivity.this, PCabActivity.class));
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddNoteActivity.this, "Ошибка добавления заметки", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }
}