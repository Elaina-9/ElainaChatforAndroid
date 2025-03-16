package com.example.elainachat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elainachat.netty.NettyClient;

public class LoginActivity extends AppCompatActivity {
    EditText hostnameEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    TextView registerTextView;
    ProgressBar progressBar;
    private NettyClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        hostnameEditText = findViewById(R.id.et_hostname);
        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        registerTextView = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progressBar);

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            // 模拟登录
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, FriendsActivity.class);
                        client = ElainaChatApplication.getInstance().getClient();
                        try {
                            client.start();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        startActivity(intent);
                    });
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
        });
    }
}