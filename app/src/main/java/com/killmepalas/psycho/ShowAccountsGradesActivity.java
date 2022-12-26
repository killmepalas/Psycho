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
import com.killmepalas.psycho.model.Account;
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;

public class ShowAccountsGradesActivity extends AppCompatActivity {

    private String tId;

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;

    private ListView lvPat, lvGrade;
    private TextView countPat;

    private DatabaseReference refTest;
    private String TEST_KEY = "Tests";

    private DatabaseReference refUsers;
    private String USERS_KEY = "Users";

    private DatabaseReference refGrades;
    private String GRADES_KEY = "Grades";

    private List<String> listPat;
    private ArrayAdapter<String> adPat;

    private List<String> listGrade;
    private ArrayAdapter<String> adGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_accounts_grades);
        init();
        getIntentStat();
        getLists();
    }

    private void init() {
        lvPat = findViewById(R.id.patListStat);
        lvGrade = findViewById(R.id.gradeListStatShow);
        countPat = findViewById(R.id.numPatStat);

        refTest = FirebaseDatabase.getInstance().getReference(TEST_KEY);
        refGrades = FirebaseDatabase.getInstance().getReference(GRADES_KEY);
        refUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        listPat = new ArrayList<>();
        listGrade = new ArrayList<>();

        adPat = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listPat);
        lvPat.setAdapter(adPat);

        adGrade = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listGrade);
        lvGrade.setAdapter(adGrade);
    }

    private void getIntentStat() {
        Intent i = getIntent();
        if (i != null) {
            tId = i.getStringExtra("tId");
        }
    }

    private void getLists() {
        refGrades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listPat.size() > 0) listPat.clear();
                if (listGrade.size() > 0) listGrade.clear();
                int k = 0;
                for (DataSnapshot ds : snapshot.child(tId).getChildren()) {
                    getUserFromDB(ds.getKey());
                    listGrade.add(ds.getValue().toString());
                    k++;

                }
                countPat.setText("Нашлось " + k + " пользователей");
                adGrade.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserFromDB(String id) {
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.child(id).getValue(Account.class);
                assert account != null;
                listPat.add(account.getEmail());
                adPat.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}