package com.max.testforrc.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.max.testforrc.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;

    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        emailTextView = findViewById(R.id.emailTextView);

        emailTextView.setText(user.getEmail());
    }
}