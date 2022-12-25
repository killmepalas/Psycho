package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class TestActivity extends AppCompatActivity {


    private Button btnCreateTest;
    private TextView numTests;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private ListView testsList;
    private DatabaseReference refTests;
    private BottomNavigationView navigation;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private List<Test> listTemp;
    private boolean psychologist, admin;


    private enum status{
        assigned,
        passed,
        created
    };

    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_created:
                        getTestsFromDB(status.created);
                        if (psychologist){
                            btnCreateTest.setVisibility(View.VISIBLE);
                            btnCreateTest.setOnClickListener(view -> {
                                Intent intent = new Intent(TestActivity.this, CreateTestActivity.class);
                                startActivity(intent);
                            });
                        }
                        return true;
                    case R.id.navigation_assigned:
                        getTestsFromDB(status.assigned);
                        btnCreateTest.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.navigation_passed:
                        btnCreateTest.setVisibility(View.INVISIBLE);
                        getTestsFromDB(status.passed);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();

        getTestsFromDB(status.assigned);
        setOnClickItem();

    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        navigation = findViewById(R.id.navigationView);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        numTests = findViewById(R.id.numTests);
        listTemp = new ArrayList<>();
        listData = new ArrayList<>();
        refTests = FirebaseDatabase.getInstance().getReference("Tests");
        testsList = findViewById(R.id.testListMy);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listData);
        testsList.setAdapter(adapter);
        btnCreateTest = findViewById(R.id.btnCreateTest);
        getIntentMain();


    }

    private void getTestsFromDB(status st){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size()>0) listData.clear();
                if (listTemp.size() > 0)listTemp.clear();
                int k = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Test test = ds.getValue(Test.class);
                    test.setId(ds.getKey());
                    assert test != null;
                    if (!st.name().equals("created")) {
                        for (DataSnapshot dss : ds.child(st.name()).getChildren()) {
                            if (dss.getKey() != null) {
                                String user = dss.getKey();
                                if (user.equals(curUser.getUid())) {
                                    listData.add(test.getName());
                                    listTemp.add(test);
                                    k++;
                                }
                            }
                        }
                    } else {
                        if(test.getPsychologistId()!= null) {
                            if (test.getPsychologistId().equals(curUser.getUid())) {
                                listData.add(test.getName());
                                listTemp.add(test);
                                k++;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if (k != 0)
                numTests.setText("Нашлось " + k + " теста(ов)");
                else if (st.name().equals("created")) numTests.setText("У вас нет ни одного созданного теста");
                else numTests.setText("Нет тестов");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refTests.addValueEventListener(valueEventListener);
    }

    private void setOnClickItem()
    {
        testsList.setOnItemClickListener((parent, view, position, id) -> {
            Test test = listTemp.get(position);
            Intent i = new Intent(TestActivity.this, ShowTestActivity.class);
            i.putExtra("testName", test.getName());
            i.putExtra("testDescription", test.getDescription());
            i.putExtra("psychologistId",test.getPsychologistId());
            i.putExtra("testId",test.getId());
            i.putExtra("testIsOpen",test.isOpen());
            startActivity(i);
        });
    }

    private void getIntentMain()
    {
        Intent i = getIntent();
        if(i != null)
        {
            psychologist = i.getBooleanExtra("psychologist",false);
            admin = i.getBooleanExtra("admin",false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        if (curUser !=null){
            if (admin){
                menu.add("Управление пользователями");
            }
            if (psychologist){
                menu.add("Мои психи");
            }
            menu.add("База тестов");
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
                Intent intent = new Intent(TestActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case "Управление пользователями": {
                Intent intent = new Intent(TestActivity.this, AdminActivity.class);
                startActivity(intent);
                break;
            }
            case "Выйти": {
                mAuth.signOut();
                Intent intent = new Intent(TestActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case "База тестов": {
                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}