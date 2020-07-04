package com.example.andrew.experiments_andrew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button b1, b2, b3, b4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.clickMe);
        b2 = (Button) findViewById(R.id.test);
        b3 = (Button) findViewById(R.id.doNotPress);
        b4 = (Button) findViewById(R.id.next);

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),"Hello!", Toast.LENGTH_LONG).show();
            }
        });

        b4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, Next.class);
                startActivity(i);
            }
        });
    }
}
