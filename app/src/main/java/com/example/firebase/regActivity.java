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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class regActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private String email, password;
    private EditText passwInp, emailInp;
    private Button signUpBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        firebaseAuth = FirebaseAuth.getInstance();
        passwInp = findViewById(R.id.editTextPassword);
        emailInp = findViewById(R.id.editTextEmail);
        signUpBut = findViewById(R.id.btnRegister);

        signUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });
    }

    private void validData() {
        email = emailInp.getText().toString().trim();
        password = passwInp.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInp.setError("Почту введи по человечески");
        } else if (TextUtils.isEmpty(password)) {
            passwInp.setError("А чего это пароль пустой у нас?");
        } else {
            firebaseRegistration();
        }
    }

    private void firebaseRegistration() {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(regActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(firebaseUser.getUid()).set(user).addOnSuccessListener(aVoid -> {
                        Toast.makeText(regActivity.this, "Пользователь добавлен в Firestore", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(regActivity.this, PCabActivity.class));
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(regActivity.this, "Ошибка добавления пользователя в Firestore", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}

