package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.killmepalas.psycho.model.Test;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Button btnBlock, btnReq;
    private ListView lvAccounts;
    private List<String> accounts;
    private TextView txt;
    private List<Account> listAc;
    private ArrayAdapter<String> adapter;

    private DatabaseReference refUsers;

    private final String USERS_KEY = "Users";
    private BottomNavigationView navigation;


    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_admin:
                getUsersFromDB(3);
                return true;
            case R.id.navigation_psychologist:
                getUsersFromDB(2);
                return true;
            case R.id.navigation_psycho:
                getUsersFromDB(1);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();
        getUsersFromDB(3);
        btnBlock.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AdminBlockActivity.class);
            startActivity(intent);
        });
        setOnClickItem();

        btnReq.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AdminReqActivity.class);
            startActivity(intent);
        });
    }

    private void setOnClickItem()
    {
        lvAccounts.setOnItemClickListener((parent, view, position, iid) -> {
            Account account = listAc.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle(account.getEmail());
            builder.setMessage("Фамилия: " + account.getLastname()+"\n"
                    + "Имя: " + account.getName()+"\n"+
                    "Отчество: " + account.getMiddlename()+"\n"+
                    "Возраст: " + account.getAge());
            if (!account.getRole().contains("3")){
                builder.setPositiveButton("Назначить админом", (dialog, id) -> refUsers.child(account.getId()).child("roles").child("3").setValue("").addOnCompleteListener(
                        task -> Toast.makeText(AdminActivity.this, "Пользователь назначен администратором", Toast.LENGTH_SHORT).show()
                ));
            } else {
                builder.setPositiveButton("Удалить из админов", (dialog, id) -> refUsers.child(account.getId()).child("roles").child("3").removeValue().addOnCompleteListener(
                        task -> Toast.makeText(AdminActivity.this, "Пользователь изгнан из администраторов", Toast.LENGTH_SHORT).show()
                ));
            }

            if (!account.getRole().contains("2")){
                builder.setNeutralButton("Назначить психологом", (dialog, id) -> refUsers.child(account.getId()).child("roles").child("2").setValue("").addOnCompleteListener(
                        task -> Toast.makeText(AdminActivity.this, "Пользователь назначен психологом", Toast.LENGTH_SHORT).show()
                ));
            } else {
                builder.setNeutralButton("Удалить из психологов", (dialog, id) -> refUsers.child(account.getId()).child("roles").child("2").removeValue().addOnCompleteListener(
                        task -> Toast.makeText(AdminActivity.this, "Пользователь изгнан из психологов", Toast.LENGTH_SHORT).show()
                ));
            }

            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(AdminActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void init(){
        lvAccounts = findViewById(R.id.lvAdmin);
        accounts = new ArrayList<>();
        listAc = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accounts);
        lvAccounts.setAdapter(adapter);
        refUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);
        navigation = findViewById(R.id.navigationAdmin);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        txt = findViewById(R.id.txtAdmin);
        btnBlock = findViewById(R.id.btnBlock);
        btnReq = findViewById(R.id.btnReq);
    }

    private void getUsersFromDB(int st){
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k = 0;
                if (listAc.size()>0) listAc.clear();
                if (accounts.size()>0) accounts.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Account account = ds.getValue(Account.class);
                    assert account != null;
                    account.setId(ds.getKey());
                    for (DataSnapshot dss: ds.child("roles").getChildren()){
                        if (dss.getKey() !=null){
                            account.setRole(dss.getKey());
                        }
                    }
                    if (account.getRole().contains(Integer.toString(st))){
                        listAc.add(account);
                        accounts.add(account.getEmail());
                        k++;
                    }
                }
                if (k != 0) txt.setText("Нашлось " + k + " пользователей");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}