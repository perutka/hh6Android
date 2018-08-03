package com.android.hh6.healhero6android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   Button button=(Button)findViewById(R.id.button7);
   button.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {

           openActivity2();
           
       }
   });
    }
    public void openActivity2(){
        Intent intent= new Intent(this,Activity2.class);
     startActivity(intent);}
    }
