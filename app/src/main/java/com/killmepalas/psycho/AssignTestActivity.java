package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssignTestActivity extends AppCompatActivity {

    private enum status {
        assign,
        revoke
    }

    private ListView lvAccounts;
    private List<String> accounts;
    private TextView txt;
    private List<Account> listAc;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String tId;

    private DatabaseReference refAssign;
    private DatabaseReference refPatients;
    private DatabaseReference refUsers;

    private final String ASSIGN_KEY = "Assigned";
    private final String PATIENTS_KEY = "Requests";
    private final String USERS_KEY = "Users";
    private BottomNavigationView navigation;


    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_assign:
                getPatientsFromDB(status.assign);
                return true;
            case R.id.navigation_revoke:
                getPatientsFromDB(status.revoke);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_test);
        getIntentTest();
        init();
        getPatientsFromDB(status.assign);
        setOnClickItem();
    }

    private void setOnClickItem() {
        lvAccounts.setOnItemClickListener((parent, view, position, iid) -> {
            Account account = listAc.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(AssignTestActivity.this);
            builder.setTitle("Тест");
            if (account.isAssign()) {
                builder.setMessage("Вы точно хотите отозвать тест для пользователя?");
                builder.setPositiveButton("Отозвать", (dialog, id) -> refAssign.child(account.getId()).setValue(false).addOnCompleteListener(
                        task -> {
                            Toast.makeText(AssignTestActivity.this, "Тест отозван", Toast.LENGTH_SHORT).show();
                            getPatientsFromDB(status.assign);
                        }
                ));
            } else {
                builder.setMessage("Вы точно хотите назначить тест пользователю?");
                builder.setPositiveButton("Назначить", (dialog, id) -> refAssign.child(account.getId()).setValue(true).addOnCompleteListener(
                        task -> {
                            Toast.makeText(AssignTestActivity.this, "Пациент удалён", Toast.LENGTH_SHORT).show();
                            getPatientsFromDB(status.revoke);
                        }
                ));

            }
            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(AssignTestActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void init() {
        lvAccounts = findViewById(R.id.lvAccountsAssign);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        accounts = new ArrayList<>();
        listAc = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accounts);
        lvAccounts.setAdapter(adapter);
        refAssign = FirebaseDatabase.getInstance().getReference(ASSIGN_KEY).child(tId);
        refPatients = FirebaseDatabase.getInstance().getReference(PATIENTS_KEY).child(curUser.getUid());
        refUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);
        navigation = findViewById(R.id.navigationAssign);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        txt = findViewById(R.id.txtAs);
    }

    private void getIntentTest() {
        Intent i = getIntent();
        if (i != null) {
            tId = i.getStringExtra("tId");
        }
    }

    private void getPatientsFromDB(status st) {
        refPatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (accounts.size() > 0) accounts.clear();
                if (listAc.size() > 0) listAc.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    boolean flag = false;
                    if (ds.getValue() != null)
                        flag = (boolean) ds.getValue();
                    if (flag) {
                        getAssignFromDB(ds.getKey(), st);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssignFromDB(String id, status st) {
        refAssign.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object ds = snapshot.child(id).getValue();
                boolean flag = false;
                if (ds != null) {
                    flag = (boolean) ds;
                }
                getUsersFromDB(id, flag, st);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsersFromDB(String id, boolean flag, status st) {
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Account account = snapshot.child(id).getValue(Account.class);
                assert account != null;
                account.setId(snapshot.child(id).getKey());
                account.setAssign(flag);
                if ((st == status.assign & !account.isAssign()) | (st == status.revoke & account.isAssign())) {
                    accounts.add(account.getLastname() + " " + account.getName() + " " + account.getMiddlename());
                    listAc.add(account);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}