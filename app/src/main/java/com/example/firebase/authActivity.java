package com.example.firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class authActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private String email, password;
    private EditText passwInp, emailInp;
    private Button signInBut;
    private TextView regActivityTransition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        firebaseAuth = FirebaseAuth.getInstance();
        emailInp = findViewById(R.id.editTextEmail);
        passwInp = findViewById(R.id.editTextPassword);
        signInBut = findViewById(R.id.btnLogin);
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });
        regActivityTransition = findViewById(R.id.txtRegisterLink);
        regActivityTransition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(authActivity.this, regActivity.class);
                startActivity(intent);
            }
        });
    }
    private void validData(){
        email = emailInp.getText().toString().trim();
        password = passwInp.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInp.setError("Почту введи по человечески");
        } else if (TextUtils.isEmpty(password)){
            passwInp.setError("А чего это пароль пустой у нас?");
        } else {
            firebaseAuthorization();
        }
    }

    private void firebaseAuthorization() {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String currentUserEmail = firebaseUser.getEmail();
                        Toast.makeText(authActivity.this, "Пользователь "+currentUserEmail + " авторизован",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(authActivity.this, PCabActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("id",firebaseUser.getUid());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(authActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(authActivity.this, PCabActivity.class));
            finish();
        }
    }
}