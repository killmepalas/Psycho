package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AdminBlockActivity extends AppCompatActivity {

    private enum status {
        active,
        block
    }

    private ListView lvAccounts;
    private List<String> accounts;
    private List<Account> listAc;
    private ArrayAdapter<String> adapter;

    private DatabaseReference refUsers;

    private final String USERS_KEY = "Users";
    private BottomNavigationView navigation;


    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_active:
                getUsersFromDB(status.active);
                return true;
            case R.id.navigation_block:
                getUsersFromDB(status.block);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_block);
        init();
        getUsersFromDB(status.active);
        setOnClickItem();
    }

    private void init() {
        lvAccounts = findViewById(R.id.lvAdminBlock);
        accounts = new ArrayList<>();
        listAc = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accounts);
        lvAccounts.setAdapter(adapter);
        refUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);
        navigation = findViewById(R.id.navigationAdminBlock);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setOnClickItem() {
        lvAccounts.setOnItemClickListener((parent, view, position, iid) -> {
            Account account = listAc.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminBlockActivity.this);
            builder.setTitle(account.getEmail());
            builder.setMessage("Фамилия: " + account.getLastname() + "\n"
                    + "Имя: " + account.getName() + "\n" +
                    "Отчество: " + account.getMiddlename() + "\n" +
                    "Возраст: " + account.getAge());
            if (account.getStatus() != null) {
                if (account.getStatus().equals("blocked")) {
                    builder.setPositiveButton("Разблокировать", (dialog, id) -> refUsers.child(account.getId()).child("status").removeValue().addOnCompleteListener(
                            task -> Toast.makeText(AdminBlockActivity.this, "Пользователь заблокирован", Toast.LENGTH_SHORT).show()
                    ));
                }
            } else {
                builder.setPositiveButton("В бан", (dialog, id) -> refUsers.child(account.getId()).child("status").setValue("blocked").addOnCompleteListener(
                        task -> Toast.makeText(AdminBlockActivity.this, "Пользователь разблокирован", Toast.LENGTH_SHORT).show()
                ));
            }

            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(AdminBlockActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void getUsersFromDB(status st) {
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listAc.size() > 0) listAc.clear();
                if (accounts.size() > 0) accounts.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    assert account != null;
                    account.setId(ds.getKey());
                    if (account.getStatus() != null) {
                        if ((st.equals(status.block) & account.getStatus().equals("blocked"))) {
                            listAc.add(account);
                            accounts.add(account.getEmail());
                        }
                    } else if (st.equals(status.active)) {
                        listAc.add(account);
                        accounts.add(account.getEmail());
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}