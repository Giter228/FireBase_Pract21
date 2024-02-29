package com.example.firebase;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PCabActivity extends AppCompatActivity {
    public static final List<Note> notesList = new ArrayList<>();
    ListView listuserNotes;
    private Button exitbtn, addbtn;
    RecyclerView notesRecycler;
    NotesAdapter noteAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcab);

        email = findViewById(R.id.emailView);
        notesRecycler = findViewById(R.id.listview);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            email.setText(firebaseUser.getEmail());
            loadUserNotes(firebaseUser.getUid());
        }
        noteAdapter = new NotesAdapter(this, notesList);
        notesRecycler.setLayoutManager(new LinearLayoutManager(this));
        notesRecycler.setAdapter(noteAdapter);
        BtnInitialize(firebaseUser);

    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadUserNotes(String userID) {
        db.collection("users").document(userID).collection("notes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                notesList.clear();
                for (QueryDocumentSnapshot noteDoc: task.getResult()) {
                    Note note = noteDoc.toObject(Note.class);
                    notesList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            } else{
                Toast.makeText(this, "Error of loading notes..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BtnInitialize(FirebaseUser firebaseUser) {
        noteAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Note clickedNote = notesList.get(position);
                Intent intent = new Intent(PCabActivity.this, DetailNoteActivity.class);
                intent.putExtra("selected_note_id", clickedNote.getId());
                intent.putExtra("selected_note_name", clickedNote.getName());
                intent.putExtra("selected_note_description", clickedNote.getDescription());
                startActivity(intent);
            }
        });

        exitbtn = findViewById(R.id.exitBTN);
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(PCabActivity.this, authActivity.class));
            }
        });

        addbtn = findViewById(R.id.addBTN);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PCabActivity.this, AddNoteActivity.class));
            }
        });
    }

}


