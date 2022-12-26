package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AdminReqActivity extends AppCompatActivity {

    private TextView txt;
    private ListView lvAccounts;
    private List<String> accounts;
    private List<Account> listAc;
    private ArrayAdapter<String> adapter;

    private DatabaseReference refUsers;
    private DatabaseReference refPsychologists;

    private final String USERS_KEY = "Users";
    private final String PSYCHOLOGISTS_KEY = "Psychologists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_req);
        init();
        getPsFromDB();
        setOnClickItem();
    }

    private void init() {
        txt = findViewById(R.id.txtReq);
        lvAccounts = findViewById(R.id.lvAdminReq);
        accounts = new ArrayList<>();
        listAc = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accounts);
        lvAccounts.setAdapter(adapter);
        refUsers = FirebaseDatabase.getInstance().getReference(USERS_KEY);
        refPsychologists = FirebaseDatabase.getInstance().getReference(PSYCHOLOGISTS_KEY);
    }

    private void getPsFromDB() {
        refPsychologists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int k = 0;
                if (listAc.size() > 0) listAc.clear();
                if (accounts.size() > 0) accounts.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if ((boolean) ds.getValue() == false) {
                        getUserFromDB(ds.getKey());
                        k++;
                    }
                }
                if (k != 0) txt.setText("Нашлось " + k + " заявок");
                adapter.notifyDataSetChanged();
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
                account.setId(id);
                for (DataSnapshot ds : snapshot.child(id).child("roles").getChildren()) {
                    if (ds.getKey() != null) {
                        account.setRole(ds.getKey());
                    }
                }
                listAc.add(account);
                accounts.add(account.getEmail());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickItem() {
        lvAccounts.setOnItemClickListener((parent, view, position, iid) -> {
            Account account = listAc.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminReqActivity.this);
            builder.setTitle("Запрос стать психологом");
            builder.setMessage("Ник" + account.getEmail() + "\n" +
                    "Фамилия: " + account.getLastname() + "\n"
                    + "Имя: " + account.getName() + "\n" +
                    "Отчество: " + account.getMiddlename() + "\n" +
                    "Возраст: " + account.getAge());
            if (!account.getRole().contains("2")) {
                builder.setPositiveButton("Назначить психологом", (dialog, id) -> {
                    refUsers.child(account.getId()).child("roles").child("2").setValue("").addOnCompleteListener(
                            task -> {
                                refPsychologists.child(account.getId()).setValue(true).addOnCompleteListener(task1 ->
                                        Toast.makeText(AdminReqActivity.this, "Пользователь назначен психологом", Toast.LENGTH_SHORT).show()
                                );
                            }
                    );
                });
            }

            builder.setNegativeButton("Назад", (dialog, id) -> Toast.makeText(AdminReqActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }
}