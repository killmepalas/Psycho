package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class PsycActivity extends AppCompatActivity {
    private TextView tReq, tPat, tStatics;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private DatabaseReference refRequests;
    private String REQUEST_KEY = "Requests";
    private ArrayList<String> listAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psyc);

        init();
        getData();

    }

    private void init(){
        tReq = findViewById(R.id.tRequestsPsyc);
        tPat = findViewById(R.id.tPatiensPsyc);
        tStatics = findViewById(R.id.tStaticsPsyc);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        listAccounts = new ArrayList<>();
        refRequests = FirebaseDatabase.getInstance().getReference().child(REQUEST_KEY).child(curUser.getUid());

        tStatics.setOnClickListener(view ->{
            Intent intent = new Intent(PsycActivity.this,StatisticsTestsActivity.class);
            startActivity(intent);
        });
    }

    private void getData(){
        refRequests.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int kReq = 0;
                int kPat = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds.getValue() == "false")
                    kReq++; else
                        kPat++;
                    listAccounts.add(ds.getKey());
                }
                if (kReq!=0){
                    tReq.setText(Integer.toString(kReq));
                    tReq.setTextColor(android.graphics.Color.rgb(0,0,139));
                    createList(tReq);
                };
                if (kPat != 0 ){
                    tPat.setText(Integer.toString(kPat));
                    tPat.setTextColor(android.graphics.Color.rgb(0,0,139));
                    createList(tPat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createList(TextView tv){
        tv.setOnClickListener(view -> {
            Intent intent = new Intent(PsycActivity.this, ListAccountsActivity.class);
            intent.putStringArrayListExtra("Accounts", listAccounts);
            startActivity(intent);
        });
    }

}