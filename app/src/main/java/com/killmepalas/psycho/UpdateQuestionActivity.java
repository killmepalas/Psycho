package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Question;

public class UpdateQuestionActivity extends AppCompatActivity {

    private String qId;
    private Question question;
    private EditText qName;
    private Button btnUpdate;
    private DatabaseReference refQuestions;
    private String QUESTIONS_KEY = "Questions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_question);
        getIntentQuestion();
        init();
        getQuestionFromDB();
        addButtonsListeners();
    }

    private void init(){
        qName = findViewById(R.id.etNameQuestionUpdate);
        btnUpdate = findViewById(R.id.btn_update_question);
        refQuestions = FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY).child(qId);
    }

    private void getIntentQuestion(){
        Intent i = getIntent();
        if (i != null){
            qId = i.getStringExtra("qId");
        }
    }

    private void getQuestionFromDB(){
        refQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                question = snapshot.getValue(Question.class);
                qName.setText(question.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addButtonsListeners(){
        btnUpdate.setOnClickListener(view -> {
            String name = qName.getText().toString();
            question.setName(name);
            refQuestions.child("name").setValue(question.getName()).addOnCompleteListener(task -> {
                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateQuestionActivity.this, ShowQuestionsActivity.class);
                intent.putExtra("tId", question.getTestId());
                startActivity(intent);
            });
        });
    }
}