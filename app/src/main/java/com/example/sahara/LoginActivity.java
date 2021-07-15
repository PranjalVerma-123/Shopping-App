package com.example.sahara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahara.Model.Users;
import com.example.sahara.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber ,Inputpassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink,NotAdminLink,ForgetPasswordLink;

    private  String parentDbName="users";
    private CheckBox checkboxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton = (Button) findViewById(R.id.login_btn);
        Inputpassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);

        checkboxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("LoginAdmin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "users";
            }
        });
    }


    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = Inputpassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credential");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessTOAccount(phone,password);

        }
    }

    private void AllowAccessTOAccount(final String phone,final String password)
    {

        if(checkboxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userphonekey, phone);
            Paper.book().write(Prevalent.userpasswodkey, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users userData = dataSnapshot.child(parentDbName).child(phone).getValue((Users.class));


                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this,"Welcome Admin, you logged in successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("users"))
                            {
                                Toast.makeText(LoginActivity.this,"logged in successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);

                            }



                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Incorrect password",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"Invalid phone number please make a account",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Account with this"+phone + "number do not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,"you need to create a new account ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}