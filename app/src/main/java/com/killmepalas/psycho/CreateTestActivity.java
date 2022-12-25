package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.killmepalas.psycho.model.Test;

public class CreateTestActivity extends AppCompatActivity {

    private EditText testName, testDescription;
    private CheckBox isOpenTest;
    private Button btnSaveTest;
    private DatabaseReference refTests;
    private DatabaseReference refUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String TESTS_KEY = "Tests";
    private String USERS_KEY = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        init();
    }

    private void init() {
        testName = findViewById(R.id.etNameTest);
        testDescription = findViewById(R.id.etDescriptionTest);
        btnSaveTest = findViewById(R.id.btn_save_test);
        isOpenTest = findViewById(R.id.cbIsOpenTest);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        refTests = FirebaseDatabase.getInstance().getReference().child(TESTS_KEY);
        refUsers = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);

        btnSaveTest.setOnClickListener(view -> {
            String name = testName.getText().toString();
            String description = testDescription.getText().toString();
            String psychologistId = curUser.getUid();
            boolean isOpen = isOpenTest.isChecked();

            Test test = new Test(name, description, psychologistId, isOpen);
            refTests.push().setValue(test).addOnCompleteListener(task -> {
                Intent intent = new Intent(CreateTestActivity.this, TestActivity.class);
                intent.putExtra("psychologist", true);
                Toast.makeText(CreateTestActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            });
        });
    }
}