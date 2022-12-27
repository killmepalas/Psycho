package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;
import com.killmepalas.psycho.model.Test;

import java.util.Objects;

public class ShowAccountActivity extends AppCompatActivity {
    private enum options {
        remove,
        add
    }

    private String userId;
    private TextView acName, acEmail, acRole, acAge, tvToPsyc;
    private Button btnAddPsychologist, btnRemovePsychologist;
    private Button btnUpdateProfile;
    private DatabaseReference userDataBase;
    private String USER_KEY = "Users";
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private DatabaseReference reqReference;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);
        getMainIntent();
        init();
        setData();
        setButtons();
    }

    public void init() {
        acEmail = findViewById(R.id.accountEmail);
        acName = findViewById(R.id.accountNames);
        acAge = findViewById(R.id.accountAge);
        acRole = findViewById(R.id.accountRole);
        tvToPsyc = findViewById(R.id.tvToPsyc);
        btnAddPsychologist = findViewById(R.id.addPsychologist);
        btnRemovePsychologist = findViewById(R.id.btnRemovePsychologist);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        userDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY).child(userId);
        reqReference = FirebaseDatabase.getInstance().getReference().child("Requests");


        tvToPsyc.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowAccountActivity.this);
            builder.setTitle("Вы уверены, что хотите стать психологом?");
            builder.setMessage("Прям точно уверены?");// заголовок
            builder.setPositiveButton("Да", (dialog, id) -> {
                DatabaseReference psycRef = FirebaseDatabase.getInstance().getReference();
                psycRef.child("Psychologists").child(curUser.getUid()).setValue(false);
                Toast.makeText(ShowAccountActivity.this, "Заявка будет рассмотрена", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Нет", (dialog, id) -> Toast.makeText(ShowAccountActivity.this, "Ну и как хотите", Toast.LENGTH_SHORT).show());
            builder.create().show();
        });
    }

    private void setData() {
        userDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                for (DataSnapshot ds : snapshot.child("roles").getChildren()) {
                    if (ds.getKey() != null) {
                        assert account != null;
                        account.setRole(ds.getKey());
                    }
                }
                assert account != null;
                if (account.getName() != null)
                    acName.setText(account.getLastname() + " " + account.getName() + " " + account.getMiddlename());
                acEmail.setText(account.getEmail());
                if (account.getAge() != null) acAge.setText("Возраст: " + account.getAge());
                if (account.getRole().contains("2")) {
                    acRole.setVisibility(View.VISIBLE);
                    tvToPsyc.setVisibility(View.INVISIBLE);
                    acRole.setText("Психолог");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setButtons() {
        searchRequest();
        if (curUser != null) {
            if (curUser.getUid().equals(userId)) btnUpdateProfile.setVisibility(View.VISIBLE);
            else if (!flag) {
                btnAddPsychologist.setVisibility(View.VISIBLE);
                btnRemovePsychologist.setVisibility(View.INVISIBLE);
            } else {
                btnRemovePsychologist.setVisibility(View.VISIBLE);
                btnAddPsychologist.setVisibility(View.INVISIBLE);
            }
        }
        btnUpdateProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ShowAccountActivity.this, AccountActivity.class);
            startActivity(intent);
        });
        btnAddPsychologist.setOnClickListener(view -> {
            setRequest(options.add);
            Toast.makeText(ShowAccountActivity.this, "Запись оформлена", Toast.LENGTH_SHORT).show();
            setButtons();
        });
        btnRemovePsychologist.setOnClickListener(view -> {
            setRequest(options.remove);
            Toast.makeText(ShowAccountActivity.this, "Запись отменена", Toast.LENGTH_SHORT).show();
            setButtons();
        });
    }

    private void setRequest(options opt) {
        if (opt == options.add) {
            reqReference.child(userId).child(curUser.getUid()).setValue(false);
            flag = true;
        } else {
            reqReference.child(userId).child(curUser.getUid()).removeValue();
            flag = false;
        }
    }

    private void getMainIntent() {
        Intent i = getIntent();
        if (i != null) {
            userId = i.getStringExtra("userId");
        }
    }

    private void searchRequest() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(userId).getChildren()) {
                    if ( curUser != null && (Objects.equals(ds.getKey(), curUser.getUid()))) {
                        flag = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reqReference.addValueEventListener(valueEventListener);
    }
}