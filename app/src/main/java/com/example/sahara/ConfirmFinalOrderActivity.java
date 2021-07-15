package com.example.sahara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sahara.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText,pincodeEditText;
    private Button confirmOrderBtn;
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        Toast.makeText(this, "Total Price = "+ totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);
        pincodeEditText = (EditText) findViewById(R.id.shipment_pincode);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });


    }

    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            nameEditText.setError("Please provide your full name");
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            phoneEditText.setError("Please provide your Mobile number");
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            addressEditText.setError("Please provide your full Address");
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            cityEditText.setError("Please provide your full name");
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            pincodeEditText.setError("Please provide your full name");
        }
        else{
            ConfirmOrder();
        }

    }

    private void ConfirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city",cityEditText.getText().toString());
        orderMap.put("pincode",pincodeEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("status","not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Order placed Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                }
                            });
                }

            }
        });
    }
}