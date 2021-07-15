package com.example.sahara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahara.Model.Users;
import com.example.sahara.Prevalent.Prevalent;
import com.example.sahara.Sellers.SellerHomeActivity;
import com.example.sahara.Sellers.SellerLoginActivity;
import com.example.sahara.Sellers.SellerRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private Button JoinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private TextView sellerBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoinNowButton  = (Button) findViewById(R.id.main_join_now_btn);
        loginButton  = (Button) findViewById(R.id.main_login_btn);
        sellerBegin = findViewById(R.id.seller_begin);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        JoinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });


        String userphonekey = Paper.book().read(Prevalent.userphonekey);
        String userpasswordkey = Paper.book().read(Prevalent.userpasswodkey);

        if(userphonekey!="" && userpasswordkey!="")
        {
            if(!TextUtils.isEmpty((userphonekey)) && !TextUtils.isEmpty(userpasswordkey))
            {
                AllowAccess(userphonekey , userpasswordkey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("users").child(phone).exists())
                {

                    Users userData = dataSnapshot.child("users").child(phone).getValue((Users.class));

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this,"logged in succesfully...",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Incorrect password",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this,"Invalid phone number please make a account",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Account with this"+ phone + "number do not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"you need to create a new account ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}