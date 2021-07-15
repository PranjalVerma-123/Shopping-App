package com.example.sahara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn,deleteBtn;
    private EditText name,price,description;

    private android.widget.ImageView imageView;

    private String productID ="";
    private DatabaseReference productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        imageView = findViewById(R.id.product_image_maintain);

        applyChangesBtn=findViewById(R.id.apply_changes_btn);
        deleteBtn=findViewById(R.id.delete_product_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);

        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletThisProduct();
            }
        });

    }

    private void deletThisProduct() {

        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(AdminMaintainProductsActivity.this, "Product Deleted successfully", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();
        if(pName.equals("")){
            price.setError("Name required");
        }
        else if(pPrice.equals("")){
            price.setError("Price required");
        }
        else if(pDescription.equals("")){
            price.setError("Description required");
        }
        else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description",pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Update Product Details successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String pName= snapshot.child("pname").getValue().toString();
                    String pDescription = snapshot.child("description").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}