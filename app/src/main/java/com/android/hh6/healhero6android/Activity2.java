package com.android.hh6.healhero6android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Activity2 extends AppCompatActivity {
    String items[]=new String[]{"Sertraline","Escitalopram","Paroxetine","Fluoxetine","Fluvoxamine"};
    ListView listView;
    Button btnEmotionWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        listView =(ListView)findViewById(R .id.list);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
   if(position==0){
       Intent myintent=new Intent(view.getContext(),Activity3.class);
       startActivityForResult(myintent,0);
   }

                    }
        });

        btnEmotionWindow = (Button) findViewById(R.id.btnEmotionWindow);
        btnEmotionWindow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Activity2.this, TakeASelfie.class);
                startActivity(intent);
            }
        });
    }


}
