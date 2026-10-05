package com.example.english_ai;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSubmit;
    private TextView tvTitle, tvToggleMode;

    private FirebaseAuth mAuth;
    private boolean isLoginMode = true; // true = התחברות, false = הרשמה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // שיוך רכיבי ה-UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvTitle = findViewById(R.id.tvTitle);
        tvToggleMode = findViewById(R.id.tvToggleMode);

        // לחיצה על כפתור התחברות / הרשמה
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAuthentication();
            }
        });

        // לחיצה על מעבר בין מצב התחברות להרשמה
        tvToggleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // בדיקה אם המשתמש כבר מחובר למערכת
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            onAuthSuccess();
        }
    }

    private void handleAuthentication() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקת תקינות הקלט
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("יש להזין אימייל");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("יש להזין סיסמה");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        if (isLoginMode) {
            // התחברות משתמש קיים
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                            onAuthSuccess();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "שגיאה בהתחברות";
                            Toast.makeText(MainActivity.this, "כישלון בהתחברות: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // הרשמת משתמש חדש
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "הרשמה הושלמה בהצלחה!", Toast.LENGTH_SHORT).show();
                            onAuthSuccess();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "שגיאה בהרשמה";
                            Toast.makeText(MainActivity.this, "כישלון בהרשמה: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvTitle.setText("התחברות");
            btnSubmit.setText("התחבר");
            tvToggleMode.setText("אין לך חשבון? הרשם כאן");
        } else {
            tvTitle.setText("הרשמה");
            btnSubmit.setText("הרשם");
            tvToggleMode.setText("יש לך כבר חשבון? התחבר כאן");
        }
    }

    private void onAuthSuccess() {
        // מעבר למסך הראשי של האפליקציה (שני את MainActivity.class לפי הצורך)
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // סגירת מסך ההתחברות שלא יחזרו אליו בלחיצה על כפתור חזור
    }
}

