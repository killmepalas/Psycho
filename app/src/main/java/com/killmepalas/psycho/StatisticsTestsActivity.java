package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsTestsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;

    private ListView lvTest, lvGrade;
    private TextView countTest;

    private DatabaseReference refTest;
    private String TEST_KEY = "Tests";

    private DatabaseReference refGrades;
    private String GRADES_KEY = "Grades";

    private List<String> listTest;
    private List<Test> listTestTemp;
    private ArrayAdapter<String> adTest;

    private List<String> listGrade;
    private ArrayAdapter<String> adGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_tests);
        init();
        getTestsFromDB();
        setOnClickItem();
    }

    private void setOnClickItem()
    {
        lvTest.setOnItemClickListener((parent, view, position, id) -> {
            Test test = listTestTemp.get(position);
            Intent i = new Intent(StatisticsTestsActivity.this, ShowAccountsGradesActivity.class);
            i.putExtra("tId",test.getId());
            startActivity(i);
        });
    }

    private void init(){
        lvTest = findViewById(R.id.testListStat);
        lvGrade = findViewById(R.id.gradeListStat);
        countTest = findViewById(R.id.numTestsStat);

        refTest = FirebaseDatabase.getInstance().getReference(TEST_KEY);
        refGrades = FirebaseDatabase.getInstance().getReference(GRADES_KEY);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        listTest = new ArrayList<>();
        listTestTemp = new ArrayList<>();
        listGrade = new ArrayList<>();

        adTest = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listTest);
        lvTest.setAdapter(adTest);

        adGrade = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listGrade);
        lvGrade.setAdapter(adGrade);

    }

    private void getTestsFromDB(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listTest.size()>0) listTest.clear();
                if (listTestTemp.size()>0) listTestTemp.clear();
                if (listGrade.size()>0) listGrade.clear();
                int k = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Test test = ds.getValue(Test.class);
                    assert test != null;
                    test.setId(ds.getKey());
                    if (test.getPsychologistId() != null && test.getPsychologistId().equals(curUser.getUid())){
                        listTest.add(test.getName());
                        listTestTemp.add(test);
                        getGradesFromDB(test.getId());
                        k++;
                    }
                }
                countTest.setText("Нашлось " + k + " теста(ов)");
                adTest.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refTest.addValueEventListener(valueEventListener);
    }

    private void getGradesFromDB(String tId){
        refGrades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long k = 0L;
                int i = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds.getKey().equals(tId)){
                        for (DataSnapshot dss: ds.getChildren()){
                            k+= (Long) dss.getValue();
                            i++;
                        }
                    }
                }
                int grade;
                if (i != 0) {
                    grade = (int) Math.round(k/i);
                    listGrade.add(Integer.toString(grade));
                }
                else listGrade.add("Нет");

                adGrade.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}