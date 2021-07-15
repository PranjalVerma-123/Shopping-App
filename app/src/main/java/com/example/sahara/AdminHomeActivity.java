package com.example.sahara;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminHomeActivity extends AppCompatActivity {

        private Button LogoutBtn,CheckOrderBtn,MaintainProductsBtn, checkApproveProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        CheckOrderBtn = (Button) findViewById(R.id.check_order_btn);
        MaintainProductsBtn = (Button) findViewById(R.id.maintain_btn);
        checkApproveProductsBtn = (Button) findViewById(R.id.check_for_approve_btn);

        MaintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);

            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        checkApproveProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminCheckNewProductsActivity.class);
                startActivity(intent);
            }
        });

        CheckOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminNewOrdersActivity.class);
                startActivity(intent);

            }
        });
    }
}