package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.R;
import com.killmepalas.psycho.model.Account;
import com.killmepalas.psycho.model.Answer;
import com.killmepalas.psycho.model.Grade;
import com.killmepalas.psycho.model.Question;
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PassTestActivity extends AppCompatActivity {

    private int indexRightAnswer = -1;
    private double grade = 0;
    private int count = 0;
    private String tId, tD, tPsyc;
    private boolean tOpen;
    private List<Question> questions;
    private List<Answer> answers;
    private Button btnNext;
    private TextView tName, qName, numQuestion, kQuestions;
    private RadioGroup rgAnswers;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private DatabaseReference refQuestions;
    private DatabaseReference refAnswers;
    private DatabaseReference refGrades;

    private String QUESTIONS_KEY = "Questions";
    private String ANSWERS_KEY = "Answers";
    private String GRADES_KEY = "Grades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_test);
        init();
        getIntentTest();
        getQuestionFromDB();

        addButtonListener();
    }

    private void init() {
        tName = findViewById(R.id.txtNameTestPass);
        qName = findViewById(R.id.txtNameQuestionPass);
        numQuestion = findViewById(R.id.txtNumQuestionPass);
        kQuestions = findViewById(R.id.txtKQPass);
        rgAnswers = findViewById(R.id.rgAnswersPass);
        btnNext = findViewById(R.id.btnNext);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        questions = new ArrayList<>();
        answers = new ArrayList<>();
        refQuestions = FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY);
        refAnswers = FirebaseDatabase.getInstance().getReference(ANSWERS_KEY);
        refGrades = FirebaseDatabase.getInstance().getReference(GRADES_KEY);
    }

    private void getIntentTest() {
        Intent i = getIntent();
        if (i != null) {
            tId = i.getStringExtra("tId");
            tName.setText(i.getStringExtra("tName"));
            tD = i.getStringExtra("testDescription");
            tOpen = i.getBooleanExtra("testIsOpen", false);
            tPsyc = i.getStringExtra("psychologistId");
        }
    }

    private void addButtonListener() {
        rgAnswers.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == indexRightAnswer){
                count = 1;
            }

            else {
                count = 0;
            }
        });

        btnNext.setOnClickListener(view -> {
            if (!(count == 0)) {
                grade++;
                indexRightAnswer = -1;
            }
            if (!(Integer.parseInt(numQuestion.getText().toString()) == questions.size()))
                getAnswersFromDB(Integer.parseInt(numQuestion.getText().toString()));
            else {
                int score = (int) Math.round ((grade / questions.size()) * 100);
                refGrades.child(tId).child(curUser.getUid()).setValue(score).addOnCompleteListener(task -> {
                    Toast.makeText(this, "Результаты записаны", Toast.LENGTH_SHORT).show();
                });

                Intent i = new Intent(PassTestActivity.this, ShowTestActivity.class);
                i.putExtra("testName", tName.getText().toString());
                i.putExtra("testDescription", tD);
                i.putExtra("psychologistId", tPsyc);
                i.putExtra("testId", tId);
                i.putExtra("testIsOpen", tOpen);
                startActivity(i);
            }
        });
    }

    private void getQuestionFromDB() {
        refQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    assert question != null;
                    question.setId(ds.getKey());
                    if (question.getTestId().equals(tId)) {
                        questions.add(question);
                        k++;
                    }
                }
                if (k == 0) {
                    btnNext.setVisibility(View.INVISIBLE);
                    qName.setText("Ой, тест пока пустой :(");
                } else {
                    kQuestions.setText(Integer.toString(k));
                }
                if (questions.size() != 0) getAnswersFromDB(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAnswersFromDB(int i) {
        qName.setText(questions.get(i).getName());
        numQuestion.setText(Integer.toString(i + 1));
        refAnswers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (answers != null) answers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Answer answer = ds.getValue(Answer.class);
                    assert answer != null;
                    answer.setId(ds.getKey());
                    if (answer.getqId().equals(questions.get(i).getId())) {
                        answers.add(answer);
                    }
                }
                addRB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addRB() {
        rgAnswers.removeAllViews();
        int k = 0;
        for (Answer a : answers) {
            RadioButton newRadioButton = new RadioButton(this);
            newRadioButton.setText(a.getName());
            newRadioButton.setId(k);
            if (a.isRight()) indexRightAnswer = k;
            rgAnswers.addView(newRadioButton);
            k++;
        }
    }
}