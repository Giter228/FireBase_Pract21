package com.example.firebase;

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

import java.util.HashMap;
import java.util.Map;

public class DetailNoteActivity extends AppCompatActivity {
    EditText et_title, et_description;
    Button buttonSave, buttonDel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDel = findViewById(R.id.buttonDel);

        et_title.setText(null);
        et_description.setText(null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            String selectedNoteId = intent.getStringExtra("selected_note_id");
            String selectedNoteName = intent.getStringExtra("selected_note_name");
            String selectedNoteDescription = intent.getStringExtra("selected_note_description");

            et_title.setText(selectedNoteName);
            et_description.setText(selectedNoteDescription);
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString().trim();
                String description = et_description.getText().toString().trim();
                if (title.isEmpty() || description.isEmpty()){
                    Toast.makeText(DetailNoteActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    String idSelectedNote = intent.getStringExtra("selected_note_id");
                    if (!idSelectedNote.isEmpty()){
                        Map<String, Object> noteUpdates = new HashMap<>();
                        noteUpdates.put("description", description);
                        noteUpdates.put("name", title);
                        db.collection("users").document(firebaseUser.getUid()).collection("notes").document(idSelectedNote).update(noteUpdates).addOnSuccessListener(task -> {
                            Toast.makeText(DetailNoteActivity.this, "Данные заметки успешно изменены", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DetailNoteActivity.this, PCabActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(DetailNoteActivity.this, "Ошибка. Данные заметки не изменены", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(DetailNoteActivity.this, "Ошибка. Запись не найдена", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idSelectedNote = intent.getStringExtra("selected_note_id");
                usersRef.document(firebaseUser.getUid()).collection("notes").document(idSelectedNote).delete().addOnSuccessListener(task ->  {
                    Toast.makeText(DetailNoteActivity.this, "Заметка успешно удалена", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailNoteActivity.this, PCabActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(DetailNoteActivity.this, "Ошибка. Заметка не удалена", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}