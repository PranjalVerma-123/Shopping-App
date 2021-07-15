package com.example.sahara.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sahara.MainActivity;
import com.example.sahara.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBegin;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private Button registerBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();

        sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn);
        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        passwordInput = findViewById(R.id.seller_password);
        emailInput = findViewById(R.id.seller_email);
        addressInput = findViewById(R.id.seller_address);
        registerBtn = findViewById(R.id.seller_register_btn);
        loadingBar = new ProgressDialog(this);


        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
        
        
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReisterSeller();
            }
        });
        
    }

    private void ReisterSeller() {

        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String address = addressInput.getText().toString();

        if(!name.equals("") && !phone.equals("")  && !email.equals("")  && !password.equals("")  && !address.equals("")){

            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage("Please wait while we are checking the credential");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        final DatabaseReference rootRef;
                        rootRef = FirebaseDatabase.getInstance().getReference();
                        String sid = mAuth.getCurrentUser().getUid();

                        HashMap<String,Object> sellerMap = new HashMap<>();

                        sellerMap.put("sid",sid);
                        sellerMap.put("name",name);
                        sellerMap.put("phone",phone);
                        sellerMap.put("email",email);
                        sellerMap.put("password",password);
                        sellerMap.put("address",address);

                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                Toast.makeText(SellerRegistrationActivity.this, "Your are register Successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
    }
}