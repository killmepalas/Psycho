package com.killmepalas.psycho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ShowAnswersActivity extends AppCompatActivity {

    private String qId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers);
        getIntentQuestion();

    }

    private void getIntentQuestion(){
        Intent intent = getIntent();
        if (intent != null){
            qId = intent.getStringExtra("qId");
        }
    }


}