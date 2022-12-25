package com.killmepalas.psycho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.killmepalas.psycho.R;
import com.killmepalas.psycho.model.Question;

public class CreateQuestionActivity extends AppCompatActivity {

    private String tId;
    private DatabaseReference refQuestions;
    private String QUESTIONS_KEY = "Questions";
    private EditText qName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        getIntentTest();
        init();
        addButtonListeners();
    }

    private void getIntentTest(){
        Intent i = getIntent();
        if (i != null){
            tId = i.getStringExtra("tId");
        }
    }

    private void init(){
        qName = findViewById(R.id.etNameQuestion);
        btnSave = findViewById(R.id.btn_save_question);
        refQuestions = FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY);
    }

    private void addButtonListeners(){
        btnSave.setOnClickListener(view -> {
            Question question = new Question(qName.getText().toString(), tId);
            refQuestions.push().setValue(question).addOnCompleteListener(task -> {
                Toast.makeText(this, "Вопрос добавлен", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreateQuestionActivity.this, ShowQuestionsActivity.class);
                i.putExtra("tId", tId);
                startActivity(i);
            });
        });
    }
}