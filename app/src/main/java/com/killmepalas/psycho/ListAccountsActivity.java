package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Question;
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;

public class ListAccountsActivity extends AppCompatActivity {

    private ArrayList<String> listAccounts;
    private boolean mod;
    private List<String> listNames;
    private List<String> listTemp;
    private TextView ac;
    private ListView lvAccounts;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;

    DatabaseReference refUser;
    private DatabaseReference refRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_accounts);

        init();
        getPsycIntent();
        setReq();
        setOnClickItem();
    }

    private void setOnClickItem() {
        lvAccounts.setOnItemClickListener((parent, view, position, iid) -> {
            String uId = listTemp.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountsActivity.this);
            builder.setTitle("Подтвердите действие");
            builder.setMessage("Действие с пользователем");// заголовок
            if (!mod)
                builder.setPositiveButton("Принять заявку", (dialog, id) -> refRequests.child(curUser.getUid()).child(uId).setValue(true).addOnCompleteListener(
                        task -> {
                            Toast.makeText(ListAccountsActivity.this, "Заявка принята", Toast.LENGTH_SHORT).show();
                            setReq();
                        }
                ));
            else if (mod)
                builder.setPositiveButton("Удалить пациента", (dialog, id) -> refRequests.child(curUser.getUid()).child(uId).setValue(false).addOnCompleteListener(
                        task -> {
                            Toast.makeText(ListAccountsActivity.this, "Пациент удалён", Toast.LENGTH_SHORT).show();
                            setReq();
                        }
                ));
            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(ListAccountsActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void init() {
        ac = findViewById(R.id.textAc);
        listAccounts = new ArrayList<>();
        listNames = new ArrayList<>();
        listTemp = new ArrayList<>();
        lvAccounts = findViewById(R.id.lvAccounts);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listNames);
        lvAccounts.setAdapter(adapter);
        refRequests = FirebaseDatabase.getInstance().getReference("Requests");
        refUser = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void getPsycIntent() {
        Intent i = getIntent();
        if (i != null) {
            listAccounts = i.getStringArrayListExtra("Accounts");
            mod = i.getBooleanExtra("mod", false);
        }
    }

    private void setUs(String id) {
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(id)){
                        String email = ds.child("email").getValue().toString();
                        String userId = ds.getKey();
                        listNames.add(email);
                        listTemp.add(userId);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setReq(){
        refRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listNames.size() > 0) listNames.clear();
                if (listTemp.size() > 0) listTemp.clear();
                for (DataSnapshot ds: snapshot.child(curUser.getUid()).getChildren()){
                    for (String id: listAccounts){
                        if (id.equals(ds.getKey()) & mod == (boolean) ds.getValue()) {
                            setUs(id);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}