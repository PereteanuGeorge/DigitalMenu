package com.example.george.digitalmenu;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import static com.example.george.digitalmenu.MenuActivity.DISH_KEY;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_info);

        Intent intent = getIntent();
        Dish dish = intent.getParcelableExtra(DISH_KEY);

        ImageView image = findViewById(R.id.imageView3);

        image.setImageBitmap(BitmapFactory.decodeByteArray(dish.getPicture(), 0, dish.getPicture().length));
    }

}
