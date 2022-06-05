package com.example.admin.stocked;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddselectorActivity extends AppCompatActivity implements View.OnClickListener {

    Button Category, Item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        Category = findViewById(R.id.CategoryButton);
        Item = findViewById(R.id.ItemButton);

        Category.setOnClickListener(this);
        Item.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.ItemButton:
                i = new Intent(this, additemActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.CategoryButton:
                i = new Intent(this, addcategoriesActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }
    }
}