package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.main.MainActivity;

public class FakeNameActivity extends AppCompatActivity {

    private String username;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fake_name_activity);

        Button button = findViewById(R.id.submit_username);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText editText = findViewById(R.id.username);
                username = editText.getText().toString().trim();
                Log.d("FakeNameActivity", "my name is " + username);
                switchToMainActivity(username);
            }
        });
    }

    public void switchToMainActivity(String username) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.USERNAME_INTENT_KEY, username);
        startActivity(intent);
    }

}
