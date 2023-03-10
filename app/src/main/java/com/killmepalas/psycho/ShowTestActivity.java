package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;
import com.killmepalas.psycho.model.Grade;

import java.util.Objects;

public class ShowTestActivity extends AppCompatActivity {

    private String grade = "0";
    private TextView tName, tDescription, tPsychologistId, tGrade;
    private Button btnPassTest, btnUpdateTest, btnDeleteTest, btnShowQuestions, btnAssignTest;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private DatabaseReference refUsers;
    private DatabaseReference refTests;
    private DatabaseReference refGrade;
    private String psychId, tId;
    private Account user;
    private boolean tIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        init();
        getIntentMain();


        refGrade.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (curUser != null) {
                    if (snapshot.child(tId).child(curUser.getUid()).getValue() != null) {
                        String grade = snapshot.child(tId).child(curUser.getUid()).getValue().toString();
                        tGrade.setText(grade);
                    }
                } else if (grade != null) tGrade.setText(grade);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getButtons();
    }

    private void init() {
        tName = findViewById(R.id.tName);
        tGrade = findViewById(R.id.tGrade);
        tDescription = findViewById(R.id.tDescription);
        tPsychologistId = findViewById(R.id.tPsychologistId);
        refUsers = FirebaseDatabase.getInstance().getReference("Users");
        refTests = FirebaseDatabase.getInstance().getReference("Tests");
        refGrade = FirebaseDatabase.getInstance().getReference("Grades");
        btnPassTest = findViewById(R.id.btnPassTest);
        btnUpdateTest = findViewById(R.id.btnUpdateTest);
        btnDeleteTest = findViewById(R.id.btnDeleteTest);
        btnShowQuestions = findViewById(R.id.btnShowQuestions);
        btnAssignTest = findViewById(R.id.btnAssignTest);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
    }

    private void getIntentMain() {
        Intent i = getIntent();
        if (i != null) {
            tName.setText(i.getStringExtra("testName"));
            tDescription.setText(i.getStringExtra("testDescription"));
            psychId = i.getStringExtra("psychologistId");
            tId = i.getStringExtra("testId");
            tIsOpen = i.getBooleanExtra("testIsOpen", true);
            grade = i.getStringExtra("score");

            getUserFromDB();
        }
    }

    private void getButtons() {
        if (curUser != null && psychId.equals(curUser.getUid())) {
            btnUpdateTest.setVisibility(View.VISIBLE);
            btnUpdateTest.setOnClickListener(view -> {
                Intent in = new Intent(ShowTestActivity.this, UpdateTestActivity.class);
                in.putExtra("tId", tId);
                startActivity(in);
            });
            btnDeleteTest.setVisibility(View.VISIBLE);
            btnDeleteTest.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowTestActivity.this);
                builder.setTitle("???? ??????????????, ?????? ???????????? ?????????????? ?????????");
                builder.setMessage("???????? ?????????? ???????????????");// ??????????????????
                builder.setPositiveButton("????", (dialog, id) -> refTests.child(tId).removeValue().addOnCompleteListener(
                        task -> {
                            Toast.makeText(ShowTestActivity.this, "???????? ?????????????? ???????? ??????", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ShowTestActivity.this, TestActivity.class);
                            i.putExtra("psychologist", true);
                            startActivity(i);
                        }
                ));
                builder.setNegativeButton("??????", (dialog, id) -> Toast.makeText(ShowTestActivity.this, "???????????????????? ??????????", Toast.LENGTH_SHORT).show());
                builder.create().show();
            });

            btnShowQuestions.setVisibility(View.VISIBLE);
            btnShowQuestions.setOnClickListener(view -> {
                Intent in = new Intent(ShowTestActivity.this, ShowQuestionsActivity.class);
                in.putExtra("tId", tId);
                startActivity(in);
            });

            btnAssignTest.setVisibility(View.VISIBLE);
            btnAssignTest.setOnClickListener(view -> {
                Intent i = new Intent(ShowTestActivity.this, AssignTestActivity.class);
                i.putExtra("tId", tId);
                startActivity(i);
            });
        }
        if (tIsOpen) {
            btnPassTest.setVisibility(View.VISIBLE);
            btnPassTest.setOnClickListener(view -> {
                Intent in = new Intent(ShowTestActivity.this, PassTestActivity.class);
                in.putExtra("tId", tId);
                in.putExtra("tName", tName.getText().toString());
                in.putExtra("testDescription", tDescription.getText().toString());
                in.putExtra("psychologistId", psychId);
                in.putExtra("testIsOpen", tIsOpen);

                startActivity(in);
            });
        } else btnPassTest.setVisibility(View.INVISIBLE);
    }

    private void getUserFromDB() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.child(psychId).getValue(Account.class);
                tPsychologistId.setText(user.getEmail());

                tPsychologistId.setOnClickListener(view -> {
                    Intent intent = new Intent(ShowTestActivity.this, ShowAccountActivity.class);
                    intent.putExtra("userId", psychId);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        menu.add("???????? ????????????");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if ("???????? ????????????".equals(title)) {
            Intent intent = new Intent(ShowTestActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}