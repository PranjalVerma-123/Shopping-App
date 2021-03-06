package com.example.sahara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahara.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;



public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText,userPhoneEditText,addreessEditText;
    private TextView profileChangeTextBtn,closeTextBtn,updateTextBtn;
    private Uri imageUri;
    private String myUrl="";
    private StorageReference storagePofilePictureRef;
    private String checker = "";
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storagePofilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView =(CircleImageView) findViewById(R.id.setting_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.setting_fullname);
        userPhoneEditText = (EditText) findViewById(R.id.setting_phone_number);
        addreessEditText = (EditText) findViewById(R.id.setting_Address);
        profileChangeTextBtn= (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn= (TextView) findViewById(R.id.close_setting);
        updateTextBtn= (TextView) findViewById(R.id.update_acount_setting);


        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addreessEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){

                    userInfoSaved();

                }
                else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("users");

        HashMap<String,Object> userMap =new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addreessEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Error! Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addreessEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Phone Number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }


    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait for a while");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null){
            final StorageReference fileRef = storagePofilePictureRef.child(Prevalent.currentOnlineUser.getPhone()+".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl=downloadUrl.toString();

                        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("users");

                        HashMap<String,Object> userMap =new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString());
                        userMap.put("address",addreessEditText.getText().toString());
                        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addreessEditText) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image =snapshot.child("image").getValue().toString();
                        String name =snapshot.child("name").getValue().toString();
                        String phone =snapshot.child("phone").getValue().toString();
                        String address =snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addreessEditText.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

//public class SettingsActivity extends AppCompatActivity {
//
//    private CircleImageView profileImageView;
//    private EditText fullNameEditText,userPhoneEditText,addreessEditText;
//    private TextView profileChangeTextBtn,closeTextBtn,updateTextBtn;
//    private Uri imageUri;
//    private String myUrl="";
//    private StorageReference storagePofilePictureRef;
//    private String checker = "";
//    private StorageTask uploadTask;
//    private Button securityQuestionsBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        storagePofilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
//
//        profileImageView =(CircleImageView) findViewById(R.id.setting_profile_image);
//        fullNameEditText = (EditText) findViewById(R.id.setting_fullname);
//        userPhoneEditText = (EditText) findViewById(R.id.setting_phone_number);
//        addreessEditText = (EditText) findViewById(R.id.setting_Address);
//        profileChangeTextBtn= (TextView) findViewById(R.id.profile_image_change_btn);
//        closeTextBtn= (TextView) findViewById(R.id.close_setting);
//        updateTextBtn= (TextView) findViewById(R.id.update_acount_setting);
//        securityQuestionsBtn = findViewById(R.id.security_question_btn);
//
//
//        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addreessEditText);
//
//
//        closeTextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//        securityQuestionsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this,ResetPasswordActivity.class);
//                intent.putExtra("check","settings");
//                startActivity(intent);
//            }
//        });
//
//        updateTextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checker.equals("clicked")){
//
//                    userInfoSaved();
//
//                }
//                else{
//                    updateOnlyUserInfo();
//                }
//            }
//        });
//
//        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checker = "clicked";
//                CropImage.activity(imageUri)
//                        .setAspectRatio(1,1)
//                        .start(SettingsActivity.this);
//            }
//        });
//
//    }
//
//    private void updateOnlyUserInfo() {
//        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("users");
//
//        HashMap<String,Object> userMap =new HashMap<>();
//        userMap.put("name",fullNameEditText.getText().toString());
//        userMap.put("address",addreessEditText.getText().toString());
//        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
//        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
//
//
//        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
//        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  && resultCode==RESULT_OK && data!=null){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//            profileImageView.setImageURI(imageUri);
//        }
//        else{
//            Toast.makeText(this, "Error! Try Again", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
//            finish();
//        }
//    }
//
//    private void userInfoSaved() {
//        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
//            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
//        }
//        else if(TextUtils.isEmpty(addreessEditText.getText().toString())){
//            Toast.makeText(this, "Address is mandatory..", Toast.LENGTH_SHORT).show();
//        }
//        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){
//            Toast.makeText(this, "Phone Number is mandatory.", Toast.LENGTH_SHORT).show();
//        }
//        else if(checker.equals("clicked")){
//            uploadImage();
//        }
//
//
//    }
//
//    private void uploadImage() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Updating Profile");
//        progressDialog.setMessage("Please wait for a while");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        if(imageUri!=null){
//            final StorageReference fileRef = storagePofilePictureRef.child(Prevalent.currentOnlineUser.getPhone()+".jpg");
//            uploadTask = fileRef.putFile(imageUri);
//
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//
//                    if(!task.isSuccessful()){
//                        throw task.getException();
//                    }
//
//                    return fileRef.getDownloadUrl();
//
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUrl = task.getResult();
//                        myUrl=downloadUrl.toString();
//
//                        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("users");
//
//                        HashMap<String,Object> userMap =new HashMap<>();
//                        userMap.put("name",fullNameEditText.getText().toString());
//                        userMap.put("address",addreessEditText.getText().toString());
//                        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
//                        userMap.put("image",myUrl);
//                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
//
//                        progressDialog.dismiss();
//                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
//                        Toast.makeText(SettingsActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                    else{
//                        progressDialog.dismiss();
//                        Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//        else{
//            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addreessEditText) {
//
//        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getPhone());
//
//        UsersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    if(snapshot.child("image").exists()){
//                        String image =snapshot.child("image").getValue().toString();
//                        String name =snapshot.child("name").getValue().toString();
//                        String phone =snapshot.child("phone").getValue().toString();
//                        String address =snapshot.child("address").getValue().toString();
//
//                        Picasso.get().load(image).into(profileImageView);
//                        fullNameEditText.setText(name);
//                        userPhoneEditText.setText(phone);
//                        addreessEditText.setText(address);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//}






//again copy from here












