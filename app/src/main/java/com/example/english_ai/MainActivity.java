package com.example.english_ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etFullName;
    private TextInputLayout tilFullName;
    private Button btnSubmit;
    private TextView tvTitle, tvToggleMode;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isLoginMode = true; // true = התחברות, false = הרשמה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // שיוך הרכיבים מה-XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        tilFullName = findViewById(R.id.tilFullName);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvTitle = findViewById(R.id.tvTitle);
        tvToggleMode = findViewById(R.id.tvToggleMode);

        // מאזינים לבלחיצות
        btnSubmit.setOnClickListener(v -> handleAuthentication());
        tvToggleMode.setOnClickListener(v -> toggleMode());
    }

    private void handleAuthentication() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("יש להזין אימייל");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("סיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        if (!isLoginMode && TextUtils.isEmpty(fullName)) {
            etFullName.setError("יש להזין שם מלא");
            return;
        }

        if (isLoginMode) {
            // התחברות
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "שגיאה בהתחברות";
                            Toast.makeText(MainActivity.this, "שגיאה: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // הרשמה
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                saveUserToFirestore(firebaseUser.getUid(), fullName, email);
                            }
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "שגיאה בהרשמה";
                            Toast.makeText(MainActivity.this, "שגיאה: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void saveUserToFirestore(String uid, String fullName, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        userMap.put("createdAt", System.currentTimeMillis());

        db.collection("Users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "הרשמה הושלמה!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "שגיאה בשמירת נתונים: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvTitle.setText("התחברות");
            btnSubmit.setText("התחבר");
            tvToggleMode.setText("אין לך חשבון? הרשם כאן");
            tilFullName.setVisibility(View.GONE);
        } else {
            tvTitle.setText("הרשמה");
            btnSubmit.setText("הרשם");
            tvToggleMode.setText("יש לך כבר חשבון? התחבר כאן");
            tilFullName.setVisibility(View.VISIBLE);
        }
    }
}