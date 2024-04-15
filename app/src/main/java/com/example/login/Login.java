package com.example.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    TextInputLayout layout_pass, layout_email;
    TextInputEditText email, password;
    TextView sign_up, forgot_password;
    Button login;
    CheckBox checkBox;

    FirebaseUser user;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize elements on the screen
        layout_email = (TextInputLayout) findViewById(R.id.et_email);
        layout_pass = (TextInputLayout) findViewById(R.id.et_password);
        email = (TextInputEditText) findViewById(R.id.et_email_layout_text);
        password = (TextInputEditText) findViewById(R.id.et_password_layout_text);
        login = (Button) findViewById(R.id.btn_login);
        checkBox = (CheckBox) findViewById(R.id.show_password);
        sign_up = (TextView) findViewById(R.id.tv_signup_button);
        forgot_password = (TextView) findViewById(R.id.tv_forgot_password);


        // click login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_str, password_str;

                email_str = String.valueOf(email.getText());
                password_str = String.valueOf(password.getText());

                // fields are empty
                if(TextUtils.isEmpty(email_str) || TextUtils.isEmpty(password_str)) {
                    if(TextUtils.isEmpty(email_str)) {
                        layout_email.setError("Please Enter your Email");
                    }
                    if(TextUtils.isEmpty(password_str)) {
                        layout_pass.setError("Please Enter your Password");
                    }
                }
                else {
                    mAuth = FirebaseAuth.getInstance();

                    // login with email and password
                    mAuth.signInWithEmailAndPassword(email_str, password_str)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    user = mAuth.getCurrentUser(); // get user

                                    if (task.isSuccessful()) {
                                        if(!user.isEmailVerified()){
                                            // email is not verified
                                            Toast.makeText(Login.this, "Please check your e-mail for verification",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(Login.this, "Login successful",
                                                    Toast.LENGTH_SHORT).show();
                                            // show user page
                                            Intent intent = new Intent(getApplicationContext(), User.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Login failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        // email Edittext field
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                // email should be entered in a specific format
                Pattern emailPattern1 = Pattern.compile("[a-zA-Z0-9._-]+@std\\.yildiz\\.edu\\.tr");
                Pattern emailPattern2 = Pattern.compile("[a-zA-Z0-9._-]+@yildiz\\.edu\\.tr");
                Matcher matcher1 = emailPattern1.matcher(email);
                Matcher matcher2 = emailPattern2.matcher(email);
                if(matcher1.matches() || matcher2.matches()){
                    layout_email.setError(""); // error text is gone while typing in correct format
                }
                else{
                    layout_email.setError("Invalid Email Format!\nex: abc@std.yildiz.edu.tr\n      abc@yildiz.edu.tr"); // wrong format
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout_pass.setError(""); // disable error while typing
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // show password toggle
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(null);
                    password.setSelection(password.length()); // cursor stays at the end of password

                } else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password.setSelection(password.length()); // cursor stays at the end of password
                }
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open signup page
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });
        // create new password
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText reset_email = new TextInputEditText(v.getContext()); // Edittext for email
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext()); // reset password with pop-up
                passwordResetDialog.setTitle("Enter your E-mail");
                passwordResetDialog.setView(reset_email);

                // continue button
                passwordResetDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = reset_email.getText().toString();
                        if(email.isEmpty()){
                            Toast.makeText(Login.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "An Error Occurred. Please Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });
                // cancel button
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
}

