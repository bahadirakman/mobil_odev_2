package com.example.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class Signup extends AppCompatActivity {
    TextInputLayout layout_name, layout_lastname, layout_id, layout_email, layout_pass;
    TextInputEditText name, lastname, id, email, password;
    Button signup;

    FirebaseAuth mAuth;

    boolean isNameValid, isLastnameValid, isIdValid, isEmailValid, isPasswordValid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // initialize elements on the screen
        layout_name = (TextInputLayout) findViewById(R.id.et_name);
        layout_lastname = (TextInputLayout) findViewById(R.id.et_lastname);
        layout_id = (TextInputLayout) findViewById(R.id.et_id);
        layout_email = (TextInputLayout) findViewById(R.id.et_email_signup);
        layout_pass = (TextInputLayout) findViewById(R.id.et_password_signup);

        name = (TextInputEditText) findViewById(R.id.et_name_layout_text);
        lastname = (TextInputEditText) findViewById(R.id.et_lastname_layout_text);
        id = (TextInputEditText) findViewById(R.id.et_id_layout_text);
        email = (TextInputEditText) findViewById(R.id.et_email_signup_layout_text);
        password = (TextInputEditText) findViewById(R.id.et_password_signup_layout_text);

        signup = (Button) findViewById(R.id.btn_signup);



        // signup button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name_str, lastname_str, email_str, id_str, password_str;
                Boolean isVerified = false;

                // get the entered fields as string
                name_str = String.valueOf(name.getText());
                lastname_str = String.valueOf(lastname.getText());
                id_str = String.valueOf(id.getText());
                email_str = String.valueOf(email.getText());
                password_str = String.valueOf(password.getText());


                // all fields are in correct format
                if (isAllValid(isNameValid, isLastnameValid, isIdValid, isEmailValid, isPasswordValid)) {
                    mAuth = FirebaseAuth.getInstance();
                    // create user
                    mAuth.createUserWithEmailAndPassword(email_str, password_str)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        // send verification e-mail
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Signup.this, "Account created. Please check your email for verification",
                                                        Toast.LENGTH_SHORT).show();
                                                // Sign in success, update UI with the signed-in user's information
                                                // store information on sharedPreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences(email_str, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("Name", name_str);
                                                editor.putString("LastName", lastname_str);
                                                editor.putString("ID", id_str);
                                                editor.putString("Email", email_str);
                                                editor.apply();
                                                // turn back to login screen
                                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Signup.this, "Verification email could not be sent",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Signup.this, "Account Creation failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else {
                    // check if fields are empty
                    if(TextUtils.isEmpty(name_str)){
                        layout_name.setError("Please Enter your Name");
                    }
                    if(TextUtils.isEmpty(lastname_str)){
                        layout_lastname.setError("Please Enter your Last Name");
                    }
                    if(TextUtils.isEmpty(id_str)){
                        layout_id.setError("Please Enter your Student ID");
                    }
                    if(TextUtils.isEmpty(email_str)){
                        layout_email.setError("Please Enter your Email Address");
                    }
                    if(!isPasswordValid){
                        layout_pass.setHelperText("");
                        layout_pass.setError("Password must contain minimum eight characters, at least one letter, at least one number and at least one special character");
                    }
                    if(TextUtils.isEmpty(password_str)){
                        layout_pass.setError("Please enter your password");
                    }
                    Toast.makeText(Signup.this, "Please enter your info correctly", Toast.LENGTH_SHORT).show();
                }

            }
        });



        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();
                Pattern namePattern = Pattern.compile("[a-zA-Z]+"); // letters only
                Matcher matcher1 = namePattern.matcher(name);

                if(matcher1.matches()){
                    layout_name.setError(""); // error text is gone while typing in correct format
                    isNameValid = true;
                }
                else{
                    layout_name.setError("Name must contain letters only"); // wrong format
                    isNameValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lastname = s.toString();
                Pattern lastnamePattern = Pattern.compile("[a-zA-Z]+"); // letters only
                Matcher matcher1 = lastnamePattern.matcher(lastname);

                if(matcher1.matches()){
                    layout_lastname.setError(""); // error text is gone while typing in correct format
                    isLastnameValid = true;
                }
                else{
                    layout_lastname.setError("Last name must contain letters only"); // wrong format
                    isLastnameValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String id = s.toString();
                Pattern idPattern = Pattern.compile("[0-9]+"); // only digits
                Matcher matcher1 = idPattern.matcher(id);

                if(matcher1.matches() && id.length() == 8){
                    layout_id.setError(""); // error text is gone while typing in correct format
                    isIdValid = true;
                }
                else{
                    layout_id.setError("Student ID must be 8 digits"); // wrong format
                    isIdValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                    isEmailValid = true;
                }
                else{
                    layout_email.setError("Invalid Email Format!\nex: abc@std.yildiz.edu.tr\n      abc@yildiz.edu.tr");  // wrong format
                    isEmailValid = false;
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
                String password = s.toString();
                // check password format
                Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&+])[A-Za-z\\d@$!%*#?&+]{8,}$");
                Matcher matcher1 = passwordPattern.matcher(password);

                isPasswordValid = matcher1.matches();
                // while typing disable error text and give info for the correct password format
                layout_pass.setError("");
                layout_pass.setHelperText("Password must contain minimum eight characters, at least one letter, at least one number and at least one special character");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // check function for all fields
    boolean isAllValid(boolean isNameValid, boolean isLastnameValid, boolean isIdValid, boolean isEmailValid, boolean isPasswordValid){
        return isNameValid && isLastnameValid && isIdValid && isEmailValid && isPasswordValid;
    }
}

