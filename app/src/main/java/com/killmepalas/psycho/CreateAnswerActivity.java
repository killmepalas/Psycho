package com.killmepalas.psycho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.killmepalas.psycho.R;
import com.killmepalas.psycho.model.Answer;

public class CreateAnswerActivity extends AppCompatActivity {

    private String qId, qName;
    private EditText aName;
    private CheckBox aIsRight;
    private Button btnCreateAnswer;
    private DatabaseReference refAnswer;
    private String ANSWERS_KEY = "Answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_answer);
        getQuestionIntent();
        init();
        addButtonsListener();
    }

    private void init(){
        aName = findViewById(R.id.etNameAnswer);
        aIsRight = findViewById(R.id.cbIsRightAnswer);
        btnCreateAnswer = findViewById(R.id.btn_save_answer);
        refAnswer = FirebaseDatabase.getInstance().getReference(ANSWERS_KEY);
    }

    private void getQuestionIntent(){
        Intent i = getIntent();
        if (i != null){
            qId = i.getStringExtra("qId");
            qName = i.getStringExtra("qName");
        }
    }

    private void addButtonsListener(){
        btnCreateAnswer.setOnClickListener(view -> {
            String name = aName.getText().toString();
            boolean isRight = aIsRight.isChecked();
            Answer answer = new Answer(name,isRight,qId);
            refAnswer.push().setValue(answer).addOnCompleteListener(task -> {
                Toast.makeText(this, "Ответ добавлен", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreateAnswerActivity.this, ShowAnswersActivity.class);
                i.putExtra("qId", qId);
                i.putExtra("qName", qName);
                startActivity(i);
            });
        });
    }
}