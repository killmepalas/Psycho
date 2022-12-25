package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.killmepalas.psycho.model.Question;
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;

public class ShowQuestionsActivity extends AppCompatActivity {

    private TextView qNum;
    private Button btnAddQuestion;
    private ListView qListView;
    private List<String> listData;
    private List<Question> listTemp;
    private ArrayAdapter<String> adapter;
    private DatabaseReference refQuestions;
    private String QUESTIONS_KEY = "Questions";
    private String tId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);
        getIntentTest();
        init();
        getQuestionsFromDB();
        setButtonsListeners();
        setOnClickItem();
    }

    private void getIntentTest() {
        Intent intent = getIntent();
        if (intent != null) {
            tId = intent.getStringExtra("tId");
        }
    }

    private void init() {
        qNum = findViewById(R.id.txtNumQuestions);
        qListView = findViewById(R.id.questionsList);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        refQuestions = FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY);

        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        qListView.setAdapter(adapter);
    }

    private void getQuestionsFromDB() {
        refQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();
                int k = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    question.setId(ds.getKey());
                    assert question != null;
                    listData.add(question.getName());
                    listTemp.add(question);
                    k++;
                }
                if (k != 0) qNum.setText("Нашлось " + k + " вопроса(ов)");
                else qNum.setText("Вопросов пока нет");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickItem() {
        qListView.setOnItemClickListener((parent, view, position, iid) -> {
            Question question = listTemp.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowQuestionsActivity.this);
            builder.setTitle("Чего изволите?");
            builder.setMessage("Вот что можем предложить:");// заголовок
            builder.setPositiveButton("Удалить вопрос", (dialog, id) -> refQuestions.child(question.getId()).removeValue().addOnCompleteListener(
                    task -> Toast.makeText(ShowQuestionsActivity.this, "Вопрос покинул этот мир", Toast.LENGTH_SHORT).show()
            ));
            builder.setNeutralButton("Редактировать", (dialog, id) -> {
                Intent i = new Intent(ShowQuestionsActivity.this, UpdateQuestionActivity.class);
                i.putExtra("qId", question.getId());
                startActivity(i);
            });
            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(ShowQuestionsActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
        qListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Question question = listTemp.get(i);
            Intent intent = new Intent(ShowQuestionsActivity.this, ShowAnswersActivity.class);
            intent.putExtra("qId", question.getId());
            startActivity(intent);
            return true;
        });
    }

    private void setButtonsListeners() {
        btnAddQuestion.setOnClickListener(view -> {
            Intent i = new Intent(ShowQuestionsActivity.this, CreateQuestionActivity.class);
            i.putExtra("tId", tId);
            startActivity(i);
        });
    }
}