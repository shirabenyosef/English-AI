package com.example.english_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity2 extends AppCompatActivity {

    // הגדרת משתנים רכיבי ממשק המשתמש (שאלה, קבוצת בחירה, כפתורים)
    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext;

    // מערך חד-ממדי המאחסן את שאלות המבחן באנגלית
    private String[] questions = {
            "1. Choose the correct word:\n'She ____ to the library every Sunday.'",
            "2. Choose the correct word:\n'They ____ football in the park yesterday.'",
            "3. Choose the correct word:\n'I have ____ finished my homework.'"
    };

    // מערך דו-ממדי המאחסן את אפשרויות התשובה לכל שאלה בהתאמה
    private String[][] options = {
            {"go", "goes", "going", "gone"},
            {"play", "plays", "played", "playing"},
            {"just", "yet", "ever", "never"}
    };

    // משנה מונה שעוקב אחרי מיקום השאלה הנוכחית במערך (מתחיל מאינדקס 0)
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // קישור בין קובץ העיצוב הגרפי (XML) לבין קוד הלוגיקה (Java)
        setContentView(R.layout.activity_2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול הרכיבים מתוך ה-XML באמצעות מזהה ה-ID הייחודי שלהם
        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNextQuestion);

        // קריאה לפונקציה שטוענת את השאלה הראשונה מיד בעליית המסך
        loadQuestion();

        // הוספת מאזין ללחיצה על כפתור המעבר לשאלה הבאה
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // בדיקת תקינות: האם המשתמש בחר תשובה כלשהי לפני המעבר
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // אם לא נבחרה תשובה, מציגים הודעת אזהרה קצרה למשתמש
                    Toast.makeText(Activity2.this, "Please select an answer!", Toast.LENGTH_SHORT).show();
                    return; // עוצרים את המשך ביצוע הקוד עד שתיבחר תשובה
                }

                // איפוס הבחירה ב-RadioGroup לקראת הצגת השאלה הבאה
                rgOptions.clearCheck();

                // קידום מונה השאלות באחד קדימה
                currentIndex++;

                // בדיקה האם יש עוד שאלות במערך
                if (currentIndex < questions.length) {
                    loadQuestion(); // אם יש עוד שאלות, טוענים אותן באותו מסך
                } else {
                    // הגענו לסוף המבחן - משתמשים ב-Intent כדי לעבור למסך השלישי (Activity3)
                    Intent intent = new Intent(Activity2.this, Activity3.class);
                    startActivity(intent);
                    finish(); // סגירת המסך הנוכחי כדי שהמשתמש לא יחזור אליו אחורה
                }
            }
        });
    }

    // פונקציית עזר שמעדכנת את הטקסטים של השאלה והאפשרויות על המסך לפי המדד הנוכחי
    private void loadQuestion() {
        tvQuestion.setText(questions[currentIndex]);
        rbOption1.setText(options[currentIndex][0]);
        rbOption2.setText(options[currentIndex][1]);
        rbOption3.setText(options[currentIndex][2]);
        rbOption4.setText(options[currentIndex][3]);
    }
}