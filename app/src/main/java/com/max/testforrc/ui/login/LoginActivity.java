package com.max.testforrc.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.max.testforrc.R;
import com.max.testforrc.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    updateUiWithUser(user);
                }
            }
        };

        System.out.println(firebaseAuth.getCurrentUser() == null ?
                "null" : firebaseAuth.getCurrentUser().getUid());
        if (firebaseAuth.getCurrentUser() != null) updateUiWithUser(firebaseAuth.getCurrentUser());

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                loginButton.setEnabled(isLoginValid(email, password));
            }
        };

        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (isLoginValid(email, password)) {
                    emailEditText.setEnabled(false);
                    passwordEditText.setEnabled(false);
                    loginButton.setEnabled(false);
                    hideKeyboard();
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    register(email, password);
                }
            }
        });
    }

    private boolean isLoginValid(String email, String password) {
        boolean isValid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.invalid_email));
            isValid = false;
        }
        if (password.length() < 8) {
            passwordEditText.setError(getString(R.string.invalid_password));
            isValid = false;
        }

        return isValid;
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
    }

    private void register(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(task.isSuccessful() + " " + task.getResult().getAdditionalUserInfo().isNewUser());

                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                            updateUiWithUser(user);
                        } else {
                            emailEditText.setEnabled(true);
                            passwordEditText.setEnabled(true);
                            loginButton.setEnabled(true);
                            loadingProgressBar.setVisibility(View.GONE);
                            showLoginFailed();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(task.isSuccessful() + " " + task.getResult().getAdditionalUserInfo().isNewUser());

                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                            updateUiWithUser(user);
                        } else {
                            emailEditText.setEnabled(true);
                            passwordEditText.setEnabled(true);
                            loginButton.setEnabled(true);
                            loadingProgressBar.setVisibility(View.GONE);
                            showLoginFailed();
                        }
                    }
                });
    }

    private void updateUiWithUser(FirebaseUser user) {
        String welcome = getString(R.string.welcome) + user.getEmail();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
    }
}