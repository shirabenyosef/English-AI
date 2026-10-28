package com.example.english_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class level_test extends AppCompatActivity {

    private TextView tvQuestion, tvQuestionCount;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext;

    private String[] questions = {
            "Choose the correct sentence:",
            "What is the past tense of 'run'?",
            "Complete the sentence: She ___ to school every day.",
            "Choose the correct synonym for 'Fast':",
            "Which word is an adjective?"
    };

    private String[][] options = {
            {"She goes to the store.", "She go to the store.", "She going to the store.", "She gone to the store."},
            {"ran", "runned", "running", "runs"},
            {"go", "goes", "gone", "going"},
            {"Quick", "Slow", "Heavy", "Tall"},
            {"Happiness", "Run", "Beautiful", "Quickly"}
    };

    private int[] correctAnswers = {0, 0, 1, 0, 2};

    private int currentQuestionIndex = 0;
    private int score = 0;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);

        loadQuestion();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // הגנה מפני קראש בלחיצה כפולה אחרי שנגמרו השאלות
                if (currentQuestionIndex >= questions.length) {
                    return;
                }

                int selectedId = rgOptions.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(level_test.this, "אנא בחר תשובה לפני המשך", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                int answerIndex = rgOptions.indexOfChild(selectedRadioButton);

                if (answerIndex == correctAnswers[currentQuestionIndex]) {
                    score++;
                }

                currentQuestionIndex++;

                if (currentQuestionIndex < questions.length) {
                    loadQuestion();
                } else {
                    // מנטרל את הכפתור כדי למנוע לחיצות נוספות בזמן השמירה
                    btnNext.setEnabled(false);
                    saveLevelAndFinish();
                }
            }
        });
    }

    private void loadQuestion() {
        rgOptions.clearCheck();
        tvQuestionCount.setText("שאלה " + (currentQuestionIndex + 1) + " מתוך " + questions.length);
        tvQuestion.setText(questions[currentQuestionIndex]);
        rbOption1.setText(options[currentQuestionIndex][0]);
        rbOption2.setText(options[currentQuestionIndex][1]);
        rbOption3.setText(options[currentQuestionIndex][2]);
        rbOption4.setText(options[currentQuestionIndex][3]);
    }

    private void saveLevelAndFinish() {
        String level;
        if (score >= 4) {
            level = "Advanced";
        } else if (score >= 2) {
            level = "Intermediate";
        } else {
            level = "Beginner";
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(level_test.this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            btnNext.setEnabled(true);
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("level", level);
        updates.put("levelTestCompleted", true);

        db.collection("users").document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(level_test.this, "המבחן הסתיים! הרמה שלך: " + level, Toast.LENGTH_LONG).show();

                    // מעבר למסך הבית HomeActivity
                    Intent intent = new Intent(level_test.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnNext.setEnabled(true);
                    Toast.makeText(level_test.this, "שגיאה בשמירת התוצאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}