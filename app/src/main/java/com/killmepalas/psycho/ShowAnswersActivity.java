package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Answer;
import com.killmepalas.psycho.model.Question;

import java.util.ArrayList;
import java.util.List;

public class ShowAnswersActivity extends AppCompatActivity {

    private String qId, qName;
    private Button btnAddAnswer;
    private ListView aList;
    private TextView tvaNum, tvqName;
    private DatabaseReference refAnswers;
    private String ANSWERS_KEY = "Answers";
    private List<String> listData;
    private List<Answer> listTemp;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers);
        getIntentQuestion();
        init();
        getAnswersFromDB();
        setOnClickItem();
        setButtonsListeners();
    }

    private void init(){
        btnAddAnswer = findViewById(R.id.btnAddAnswer);
        tvaNum = findViewById(R.id.txtNumAnswers);
        tvqName = findViewById(R.id.txtNameQuestion_showAnswers);
        aList = findViewById(R.id.answersList);
        refAnswers = FirebaseDatabase.getInstance().getReference(ANSWERS_KEY);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        aList.setAdapter(adapter);
        tvqName.setText(qName);
    }

    private void getIntentQuestion(){
        Intent intent = getIntent();
        if (intent != null){
            qId = intent.getStringExtra("qId");
            qName = intent.getStringExtra("qName");

        }
    }

    private void getAnswersFromDB() {
        refAnswers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();
                int k = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Answer answer = ds.getValue(Answer.class);
                    assert answer != null;
                    if (answer.getqId().equals(qId)){
                    answer.setId(ds.getKey());

                    listData.add(answer.getName());
                    listTemp.add(answer);
                    k++;}
                }
                if (k != 0) tvaNum.setText("Нашлось " + k + " ответа(ов)");
                else tvaNum.setText("Ответов пока нет");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickItem() {
        aList.setOnItemClickListener((parent, view, position, iid) -> {
            Answer answer = listTemp.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowAnswersActivity.this);
            builder.setTitle("Чего изволите?");
            builder.setMessage("Вот что можем предложить:");// заголовок
            builder.setPositiveButton("Удалить ответ", (dialog, id) -> refAnswers.child(answer.getId()).removeValue().addOnCompleteListener(
                    task -> Toast.makeText(ShowAnswersActivity.this, "Ответ покинул этот мир", Toast.LENGTH_SHORT).show()
            ));
            builder.setNeutralButton("Редактировать", (dialog, id) -> {
                Intent i = new Intent(ShowAnswersActivity.this, UpdateAnswerActivity.class);
                i.putExtra("aId", answer.getId());
                i.putExtra("qId", qId);
                i.putExtra("qName", qName);
                startActivity(i);
            });
            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(ShowAnswersActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void setButtonsListeners() {
        btnAddAnswer.setOnClickListener(view -> {
            Intent i = new Intent(ShowAnswersActivity.this, CreateAnswerActivity.class);
            i.putExtra("qId", qId);
            i.putExtra("qName", qName);
            startActivity(i);
        });
    }


}