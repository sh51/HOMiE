package com.cs65.homie.ui.login.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cs65.homie.FirebaseHelper;
import com.cs65.homie.Globals;
import com.cs65.homie.R;
import com.cs65.homie.models.Profile;
import com.cs65.homie.ui.ProfileSettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegistrationActivity extends AppCompatActivity {
    public static final String KEY_EMAIL = "EMAIL";
    private LoginViewModel loginViewModel;
    private final static int CREATE_PROFILE = 1;
    private EditText usernameEditText, passwordEditText;
    private Button createAccount_Button;
    private FirebaseAuth mAuth;
    private FirebaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        createAccount_Button = findViewById(R.id.createAccount);

        // Initialize Firebase Auth, a shared instance
        mAuth = FirebaseAuth.getInstance();
        // also helper
        mHelper = FirebaseHelper.getInstance();
        // disabled at first as the fields are empty
        createAccount_Button.setEnabled(false);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                createAccount_Button.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }

        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(Globals.TAG, "RegistrationEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
                    if (createAccount_Button.isEnabled()) onCreateAccount(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        createAccount_Button.setOnClickListener(v -> {
            onCreateAccount(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // TD: handle the case where the user is already logged in
        }
    }

    // go to dashboard after successful registration
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PROFILE) {
            Log.d(Globals.TAG, "Successful registration.");
            finish();
        }
    }

    private boolean onCreateAccount(String email, String password) {
//        loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                passwordEditText.getText().toString());
        // create account with Firebase auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Globals.TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Navigate to CreateProfile
                            Intent intent = new Intent(RegistrationActivity.this, ProfileSettingsActivity.class);
                            intent.putExtra(KEY_EMAIL, email);

                            // create a default account right away
                            Profile newProfile = new Profile();
                            newProfile.setId(user.getUid());
                            newProfile.setEmail(email);
                            newProfile.setPassword(password);
                            mHelper.createProfile(newProfile);

                            startActivityForResult(intent, CREATE_PROFILE);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(Globals.TAG, "createUserWithEmail:failure", task.getException());
                            finish();
                        }

                        // ...
                    }
                });


        return true;
    }

}