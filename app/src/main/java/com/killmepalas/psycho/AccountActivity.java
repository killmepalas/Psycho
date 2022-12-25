package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;

import java.io.IOException;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {
    private EditText edEmail;
    private EditText edName;
    private EditText edLastname;
    private EditText edMiddlename;
    private EditText edAge;
    private Button btnPassword;
    private FirebaseUser curUser;
    private Button btnDelete;
    private Button btnSave;
    private DatabaseReference userDataBase;
    private String USER_KEY="Users";
    private FirebaseAuth mAuth;

    private String userPassword;
    private String userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        init();
        setData();

        btnSave.setOnClickListener(
                view -> {
                        String name = edName.getText().toString();
                        String lastname = edLastname.getText().toString();
                        String middlename = edMiddlename.getText().toString();
                        String email = edEmail.getText().toString();
                        Long age = Long.valueOf(edAge.getText().toString());
                        userDataBase.child("email").setValue(email);
                        userDataBase.child("name").setValue(name);
                        userDataBase.child("middlename").setValue(middlename);
                        userDataBase.child("lastname").setValue(lastname);
                        userDataBase.child("age").setValue(age);
                    Toast.makeText(AccountActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                }
        );

        btnDelete.setOnClickListener(
                view ->
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    builder.setTitle("Вы уверены, что хотите удалить профиль?");
                    builder.setMessage("Прям точно уверены?");// заголовок
                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            userDataBase.child("status").setValue("deleted");
                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(AccountActivity.this, "Правильный выбор", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.create().show();
                }
        );

        btnPassword.setOnClickListener(
                view ->{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_new_password, null);
                    builder.setView(dialogView)
                            .setTitle("Смена пароля")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EditText lastPassword = dialogView.findViewById(R.id.oldPassword_dialog);
                                    EditText newPassword = dialogView.findViewById(R.id.newPassword_dialog);
                                    if (lastPassword == null){Toast.makeText(AccountActivity.this,"vse ploho",Toast.LENGTH_SHORT).show();}
                                    if (lastPassword.getText().toString().equals(newPassword.getText().toString())){
                                        Toast.makeText(AccountActivity.this, newPassword.getText().toString() + " and " + lastPassword.getText().toString() ,Toast.LENGTH_SHORT).show();
                                    } else if (!lastPassword.getText().toString().trim().equals(userPassword)){
                                        Toast.makeText(AccountActivity.this,"Текущий пароль введён неверно",Toast.LENGTH_SHORT).show();
                                    } else {
                                        userDataBase.child("password").setValue(newPassword.getText().toString().trim());
                                        curUser.updatePassword(newPassword.getText().toString().trim())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(AccountActivity.this,"Пароль успешно сменён",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            })
                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(AccountActivity.this,"Пароль не сменён",Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.create().show();
                }
        );
    }

    private void setData(){
        userDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                for (DataSnapshot ds: snapshot.child("roles").getChildren()){
                    if (ds.getKey() !=null){
                        account.setRole(ds.getKey());}
                }
                userPassword = account.getPassword();
                userRole = account.getRole().toString();
                edName.setText(account.getName());
                edLastname.setText(account.getLastname());
                edMiddlename.setText(account.getMiddlename());
                edEmail.setText(account.getEmail());
                if (account.getAge()!=null) edAge.setText(String.valueOf(account.getAge()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void init(){
        edEmail = findViewById(R.id.email_account);
        edName = findViewById(R.id.name_account);
        edLastname = findViewById(R.id.lastname_account);
        edMiddlename = findViewById(R.id.middle_name_account);
        edAge = findViewById(R.id.age_account);
        btnPassword = findViewById(R.id.btn_password_account);
        btnSave = findViewById(R.id.btn_save_account);
        btnDelete = findViewById(R.id.btn_delete_account);
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        userDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY).child(curUser.getUid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        if (curUser !=null){
            if (userRole.contains("3")){
                menu.add("Управление пользователями");
            }
            if (userRole.contains("2")){
                menu.add("Мои психи");
            }
            menu.add("База тестов");
            menu.add("Мои тесты");
            menu.add("Выйти");
        } else {
            menu.add("Войти");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        switch (title) {
            case "Управление пользователями": {
                Intent intent = new Intent(AccountActivity.this, AdminActivity.class);
                startActivity(intent);
                break;
            }
            case "Мои психи": {
                Intent intent = new Intent(AccountActivity.this, PsycActivity.class);
                startActivity(intent);
                break;
            }
            case "Мои тесты": {
                Intent intent = new Intent(AccountActivity.this, TestActivity.class);
                startActivity(intent);
                break;
            }
            case "Выйти": {
                mAuth.signOut();
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case "База тестов": {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}