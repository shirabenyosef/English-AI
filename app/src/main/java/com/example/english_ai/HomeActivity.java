
package com.example.english_ai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    // משתנים המייצגים את הכפתורים במסך הבית
    private Button btnVocabulary;
    private Button btnGrammar;
    private Button btnWriting;
    private Button btnProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // טעינת העיצוב של מסך הבית
        setContentView(R.layout.home_activity);

        // קישור בין הכפתורים בקוד Java לכפתורים שבקובץ XML
        btnVocabulary = findViewById(R.id.btnVocabulary);
        btnGrammar = findViewById(R.id.btnGrammar);
        btnWriting = findViewById(R.id.btnWriting);
        btnProgress = findViewById(R.id.btnProgress);

        // פעולה המתבצעת בלחיצה על כפתור אוצר מילים
        btnVocabulary.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this,"בחרת אוצר מילים",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // פעולה המתבצעת בלחיצה על כפתור דקדוק
        btnGrammar.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "בחרת דקדוק",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // פעולה המתבצעת בלחיצה על כפתור כתיבה
        btnWriting.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "בחרת כתיבה",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // פעולה המתבצעת בלחיצה על כפתור היסטוריה והתקדמות
        btnProgress.setOnClickListener(v ->
                Toast.makeText(
                        HomeActivity.this,
                        "בחרת היסטוריה והתקדמות",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}
