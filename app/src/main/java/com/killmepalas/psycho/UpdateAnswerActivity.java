package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Answer;

public class UpdateAnswerActivity extends AppCompatActivity {

    private Answer answer;
    private String aId, qId, qName;
    private EditText aName;
    private CheckBox aIsRight;
    private Button btnUpdate;
    private DatabaseReference refAnswers;
    private String ANSWERS_KEY = "Answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_answer);
        getIntentAnswer();
        init();
        getAnswerFromDB();
        addButtonsListener();
    }

    private void getIntentAnswer(){
        Intent i = getIntent();
        if (i != null){
            aId = i.getStringExtra("aId");
            qId = i.getStringExtra("qId");
            qName = i.getStringExtra("qName");
        }
    }

    private void init(){
        aName = findViewById(R.id.etNameAnswerUpdate);
        aIsRight = findViewById(R.id.cbIsRightAnswerUpdate);
        btnUpdate = findViewById(R.id.btn_update_answer);
        refAnswers = FirebaseDatabase.getInstance().getReference(ANSWERS_KEY).child(aId);
    }

    private void getAnswerFromDB(){
        refAnswers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                answer = snapshot.getValue(Answer.class);
                aName.setText(answer.getName());
                if (answer.isRight()) aIsRight.isChecked();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addButtonsListener(){
        btnUpdate.setOnClickListener(view -> {
            String name = aName.getText().toString();
            boolean isRight = aIsRight.isChecked();
            answer.setName(name);
            answer.setRight(isRight);
            refAnswers.child("name").setValue(answer.getName());
            refAnswers.child("right").setValue(answer.isRight()).addOnCompleteListener(task -> {
                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateAnswerActivity.this, ShowAnswersActivity.class);
                intent.putExtra("qId", qId);
                intent.putExtra("qName", qName);
                startActivity(intent);
            });
        });
    }
}