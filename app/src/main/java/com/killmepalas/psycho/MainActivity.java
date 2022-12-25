package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Account user;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private TextView selection;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private ListView testsList;
    private DatabaseReference refUsers;
    private DatabaseReference refTests;
    private List<Test> listTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getTestsFromDB();
        if (curUser != null){
        refUsers = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        getUserFromDatabase();
        };
        setOnClickItem();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        listTemp = new ArrayList<>();
        curUser = mAuth.getCurrentUser();
        selection = findViewById(R.id.selection);
        refTests = FirebaseDatabase.getInstance().getReference("Tests");
        listData = new ArrayList<>();
        testsList = findViewById(R.id.testList);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listData);
        testsList.setAdapter(adapter);
    }

    private void getTestsFromDB(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size()>0) listData.clear();
                if (listTemp.size()>0) listTemp.clear();
                int k = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Test test = ds.getValue(Test.class);
                    test.setId(ds.getKey());
                    assert test != null;
                    listData.add(test.getName());
                    listTemp.add(test);
                    k++;
                }
                selection.setText("Нашлось " + k + " теста(ов)");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refTests.addValueEventListener(valueEventListener);
    }

    private void getUserFromDatabase(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Account.class);
                for (DataSnapshot ds: snapshot.child("roles").getChildren()){
                    if (ds.getKey() !=null){
                    user.setRole(ds.getKey());}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addValueEventListener(valueEventListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        if (user !=null){
            if (user.getRole().contains("3")){
                menu.add("Управление пользователями");
            }
            if (user.getRole().contains("2")){
                menu.add("Мои психи");
            }
            menu.add("Мой профиль");
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
            case "Войти": {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case "Мои психи": {
                Intent intent = new Intent(MainActivity.this, PsycActivity.class);
                startActivity(intent);
                break;
            }
            case "Управление пользователями": {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                break;
            }
            case "Мои тесты": {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("psychologist", user.getRole().contains("2"));
                intent.putExtra("admin", user.getRole().contains("3"));
                startActivity(intent);
                break;
            }
            case "Выйти": {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case "Мой профиль": {
                Intent intent = new Intent(MainActivity.this, ShowAccountActivity.class);
                intent.putExtra("userId", curUser.getUid());
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnClickItem()
    {
        testsList.setOnItemClickListener((parent, view, position, id) -> {
            Test test = listTemp.get(position);
            Intent i = new Intent(MainActivity.this, ShowTestActivity.class);
            i.putExtra("testName", test.getName());
            i.putExtra("testDescription", test.getDescription());
            i.putExtra("psychologistId",test.getPsychologistId());
            i.putExtra("testId",test.getId());
            i.putExtra("testIsOpen",test.isOpen());
            startActivity(i);
        });
    }
}