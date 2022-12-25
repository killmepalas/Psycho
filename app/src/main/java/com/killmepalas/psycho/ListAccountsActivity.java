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

import java.util.ArrayList;
import java.util.List;

public class ListAccountsActivity extends AppCompatActivity {

    private ArrayList<String> listAccounts;
    private List<String> listNames;
    private TextView ac;
    private ListView lvAccounts;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_accounts);

        init();
        getPsycIntent();
        setList();
    }

    private void init(){
        ac = findViewById(R.id.textAc);
        listAccounts = new ArrayList<>();
        listNames = new ArrayList<>();
        lvAccounts = findViewById(R.id.lvAccounts);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listNames);
        lvAccounts.setAdapter(adapter);
    }

    private void getPsycIntent(){
        Intent i = getIntent();
        if (i != null){
            listAccounts = i.getStringArrayListExtra("Accounts");
        }
    }

    private void setList(){
        for (String id: listAccounts){

            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
            refUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = snapshot.child("email").getValue().toString();
                    listNames.add(email);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}