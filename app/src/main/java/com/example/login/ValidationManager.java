package com.example.login;

import android.content.Context;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ValidationManager{
    private static ValidationManager instance = null;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private ErrorSetter errorSetter;
    private final String ERR_MSG_CHECHK_EMAIL = "Invalid email format.";


    private boolean isEmailValid = false;

    private ValidationManager(){}
    public static ValidationManager getInstance(){
        if(instance == null){
            instance = new ValidationManager();
        }
        return instance;
    }
    interface ErrorSetter{
        void setError(TextInputLayout textInputLayout, String errorMessage);
    }

    ValidationManager doValidation(Context context, TextInputLayout textInputLayout){
        errorSetter = (ErrorSetter) context;
        this.textInputLayout = textInputLayout;
        this.editText = textInputLayout.getEditText();
        return instance;

    }
    ValidationManager checkEmail(){
        if(!editText.getText().toString().matches(emailPattern)){
            isEmailValid = false;
        }
        else{
            isEmailValid = true;
        }
        return instance;
    }
}