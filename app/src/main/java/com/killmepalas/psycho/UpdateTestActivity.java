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
import com.killmepalas.psycho.model.Test;

public class UpdateTestActivity extends AppCompatActivity {

    private EditText tName, tDescription;
    private CheckBox tIsOpen;
    private Button btnUpdate;

    private DatabaseReference refTests;
    private String TESTS_KEY = "Tests";
    private Test test;
    private String tId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_test);

        getTestIntent();
        init();
        setData();
        setButton();
    }

    private void getTestIntent(){
        Intent i = getIntent();
        if (i != null){
            tId = i.getStringExtra("tId");
        }
    }

    private void init(){
        tName = findViewById(R.id.etNameTestUpdate);
        tDescription = findViewById(R.id.etDescriptionTestUpdate);
        tIsOpen = findViewById(R.id.cbIsOpenTestUpdate);
        btnUpdate = findViewById(R.id.btn_update_test);

        refTests = FirebaseDatabase.getInstance().getReference(TESTS_KEY).child(tId);
    }

    private void setData(){
        refTests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                test = snapshot.getValue(Test.class);
                test.setId(snapshot.getKey());
                tName.setText(test.getName());
                tDescription.setText(test.getDescription());
                tIsOpen.setChecked(test.isOpen());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setButton(){
        btnUpdate.setOnClickListener(view -> {
            String name = tName.getText().toString();
            String description = tDescription.getText().toString();
            boolean isOpen = tIsOpen.isChecked();
            refTests.child("name"). setValue(name);
            refTests.child("description"). setValue(description);
            refTests.child("open"). setValue(isOpen)
            .addOnCompleteListener(task -> {
                Toast.makeText(UpdateTestActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UpdateTestActivity.this, ShowTestActivity.class);
                i.putExtra("testName", test.getName());
                i.putExtra("testDescription", test.getDescription());
                i.putExtra("psychologistId",test.getPsychologistId());
                i.putExtra("testId",test.getId());
                i.putExtra("testIsOpen",test.isOpen());
                startActivity(i);
            });
        });
    }
}