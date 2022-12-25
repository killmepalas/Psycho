package com.killmepalas.psycho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.killmepalas.psycho.model.Account;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText password_login;
    private Button btn_login;
    private TextView register_txt;
    private FirebaseAuth mAuth;
    private TextView no_login_txt;
    private TextView whoLogin;

    private String status;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.btn_login);
        register_txt = findViewById(R.id.register_txt);
        no_login_txt = findViewById(R.id.no_login_txt);
        whoLogin = findViewById(R.id.who_login);

        mAuth = FirebaseAuth.getInstance();

        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        no_login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email_login.getText().toString().isEmpty() || password_login.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email_login.getText().toString().trim(),password_login.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.child(mAuth.getCurrentUser().getUid()).child("status").getValue() != null)
                                                status = snapshot.child(mAuth.getCurrentUser().getUid()).child("status").getValue().toString();
                                                else status="Здравствуйте, " + snapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue().toString();
                                                whoLogin.setText(status);
                                                if (status.equals("deleted")) {
                                                    Toast.makeText(LoginActivity.this, "Профиль был удалён", Toast.LENGTH_SHORT).show();
                                                }
                                                else if (status.equals("blocked")) {
                                                    Toast.makeText(LoginActivity.this, "Профиль был заморожен", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}