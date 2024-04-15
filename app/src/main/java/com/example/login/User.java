package com.example.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class User extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView user_info;

    Button btn_logout;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // initialize fields on the screen
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        btn_logout = findViewById(R.id.btn_logout);
        user_info = findViewById(R.id.textView2);

        // if user does not exist, terminate
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else{
            String user_email = user.getEmail();
            // find the info of user from sharedPreferences
            SharedPreferences sp = getApplicationContext().getSharedPreferences(user_email, MODE_PRIVATE);
            String user_name = sp.getString("Name", "");
            String user_lastname = sp.getString("LastName", "");
            String user_id = sp.getString("ID", "");

            // print the info
            String full_info = user_name + "\n" + user_lastname + "\n" + user_id +"\n" + user_email;
            user_info.setText(full_info);
        }
        // log out button
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // return to login page
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
