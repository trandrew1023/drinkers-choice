package com.coms309.drinkerschoice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseCreatAccountActivity extends AppCompatActivity {
    private Button drinker;
    private Button driver;
    private Button business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_creat_account);
        setButtons();
    }

    /**
     * sets each create account button to open the correct activity
     */
    private void setButtons() {
        drinker = findViewById(R.id.activity_create_button1);
        driver = findViewById(R.id.activity_create_button2);
        business = findViewById(R.id.activity_create_button3);

        drinker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseCreatAccountActivity.this, CreateAccountActivity.class);
                startActivity(i);
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseCreatAccountActivity.this, DriverCreateAccountActivity.class);
                startActivity(i);
            }
        });

        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseCreatAccountActivity.this, BusinessCreateAccountActivity.class);
                startActivity(i);
            }
        });
    }
}
