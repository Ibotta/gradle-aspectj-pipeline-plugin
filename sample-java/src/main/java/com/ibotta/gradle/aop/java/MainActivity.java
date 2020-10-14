package com.ibotta.gradle.aop.java;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MessageListener {
    private Button bButtonJava;
    private TextView tvMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bButtonJava = findViewById(R.id.b_button_java);
        tvMessage = findViewById(R.id.tv_message);

        bButtonJava.setOnClickListener(view -> {
            tvMessage.setText("");
            new JavaTargetExample().demonstrateJavaAOP(MainActivity.this);
        });
    }

    @Override
    public void onMessage(String message, CallerType callerType) {
        tvMessage.append(message + "\n");
    }
}
