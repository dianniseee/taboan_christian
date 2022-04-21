package com.example.taboan_capstone.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.customer.CustomerRegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.Objects;

public class AdminRegisterSellerActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private FirebaseAuth firebaseAuth;

    private Uri image_uri;

    private String[] cameraPermissions;
    private String[] storagePermission;

    private ImageView sellerCover;
    private TextInputLayout sellerFName,sellerLName,sellerAddress,sellerMobile,sellerPersonalEmail,sellerStoreName
            ,sellerStoreLocation,sellerStoreMarket,sellerStoreEmail,sellerPassword,sellerConfirmPassword
            ,sellerMarketCateogry;
    private TextInputEditText admin_seller_market_location_onTouch,admin_seller_market_category_onTouch;
    private Button sellerRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_seller);

        sellerCover = findViewById(R.id.admin_seller_cover);
        sellerFName = findViewById(R.id.admin_seller_fName);
        sellerLName = findViewById(R.id.admin_seller_lName);
        sellerAddress = findViewById(R.id.admin_seller_Address);
        sellerMobile = findViewById(R.id.admin_seller_Mobile);
        sellerPersonalEmail = findViewById(R.id.admin_seller_personal_Email);
        sellerStoreName = findViewById(R.id.admin_seller_store_name);
        sellerStoreLocation = findViewById(R.id.admin_seller_store_location);
        sellerStoreMarket = findViewById(R.id.admin_seller_market_location);
        sellerStoreEmail = findViewById(R.id.admin_seller_store_email);
        sellerPassword = findViewById(R.id.admin_seller_password);
        sellerConfirmPassword = findViewById(R.id.admin_seller_confirm_password);
        sellerRegister = findViewById(R.id.admin_seller_register);
        sellerMarketCateogry = findViewById(R.id.admin_seller_market_category);
        admin_seller_market_category_onTouch = findViewById(R.id.admin_seller_market_category_onTouch);
        admin_seller_market_location_onTouch = findViewById(R.id.admin_seller_market_location_onTouch);

        firebaseAuth = FirebaseAuth.getInstance();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        sellerCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showImagePickDialog(); }
        });

        sellerStoreMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { categoryMarketPlace(); }
        });

        sellerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { registerSeller(); }
        });

        admin_seller_market_location_onTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryMarketPlace();
            }
        });

        admin_seller_market_category_onTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marketCategory();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this,"Camera permissions is necessary", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }

        switch(requestCode){
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if( storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this,"Storage permissions is necessary", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                sellerCover.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                image_uri = data.getData();
                sellerCover.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void categoryMarketPlace() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Markets")
                .setItems(Constants.Companion.getMARKET_PLACE(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String category = Constants.Companion.getMARKET_PLACE()[i];
                        sellerStoreMarket.getEditText().setText(category);
                    }
                }).show();

    }

    private void marketCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Markets")
                .setItems(Constants.Companion.getMARKET_CATEGORY(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String category = Constants.Companion.getMARKET_CATEGORY()[i];
                        sellerMarketCateogry.getEditText().setText(category);
                    }
                }).show();

    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        if (which == 0) {
                            if (checkCameraPermission()) { pickFromCamera(); }
                            else { requestCameraPermission(); }
                        } else {
                            if (checkStoragePermission()) { pickFromGallery(); }
                            else { requestStoragePermission(); }
                        }
                    }
                })
                .show();
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private  String regSelFName,regSelLName,regSelAddress,selRegMobile,selRegPersonalEmail,selRegStoreName,selRegStoreLocation,
                    selRegStoreMarket,selRegStoreEmail,selRegPassword,selRegConfirmPassword,sellerMarketCategory;
    private void registerSeller(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

       boolean imageLoaded = hasImage(sellerCover);

        regSelFName = Objects.requireNonNull(sellerFName.getEditText()).getText().toString().trim();
        regSelLName = Objects.requireNonNull(sellerLName.getEditText()).getText().toString().trim();
        regSelAddress = Objects.requireNonNull(sellerAddress.getEditText()).getText().toString().trim();
        selRegMobile = Objects.requireNonNull(sellerMobile.getEditText()).getText().toString().trim();
        selRegPersonalEmail = Objects.requireNonNull(sellerPersonalEmail.getEditText()).getText().toString().trim();
        selRegStoreName = Objects.requireNonNull(sellerStoreName.getEditText()).getText().toString().trim();
        selRegStoreLocation = Objects.requireNonNull(sellerStoreLocation.getEditText()).getText().toString().trim();
        selRegStoreMarket = Objects.requireNonNull(sellerStoreMarket.getEditText()).getText().toString().trim();
        sellerMarketCategory = Objects.requireNonNull(sellerMarketCateogry.getEditText()).getText().toString().trim();
        selRegStoreEmail = Objects.requireNonNull(sellerStoreEmail.getEditText()).getText().toString().trim();
        selRegPassword = Objects.requireNonNull(sellerPassword.getEditText()).getText().toString().trim();
        selRegConfirmPassword = Objects.requireNonNull(sellerConfirmPassword.getEditText()).getText().toString().trim();

       if(imageLoaded){

       }else{
           Toast.makeText(AdminRegisterSellerActivity.this, "Cover photo must be included!",Toast.LENGTH_SHORT).show();
       }

        if (TextUtils.isEmpty(regSelFName)) {
            sellerFName.setError("Required First Name");
            return;
        }
        else{
            sellerFName.setError(null);
        }
        if (TextUtils.isEmpty(regSelLName)) {
            sellerLName.setError("Required your Last Name");
            return;
        }else{
            sellerLName.setError(null);
        }
        if (TextUtils.isEmpty(regSelAddress)) {
            sellerAddress.setError("Required address");
            return;
        }
        else{
            sellerAddress.setError(null);
        }
        if (TextUtils.isEmpty(selRegMobile)) {
            sellerMobile.setError("Require Mobile Number");
            return;
        }
        else{
            sellerMobile.setError(null);
        }
        if (TextUtils.isEmpty(selRegPersonalEmail)) {
            sellerPersonalEmail.setError("Required Personal Email");
            return;
        }
        else{
            sellerPersonalEmail.setError(null);
        }
        if (TextUtils.isEmpty(selRegStoreName)) {
            sellerStoreName.setError("Required Store Name");
        }else{
            sellerStoreName.setError(null);
        }
        if (TextUtils.isEmpty(selRegStoreLocation)) {
            sellerStoreLocation.setError("Required Store Location");
            return;
        }
        else{
            sellerStoreLocation.setError(null);
        }
        if (TextUtils.isEmpty(selRegStoreMarket)) {
            sellerStoreMarket.setError("Required Store Market");
            return;
        }
        else{
            sellerStoreMarket.setError(null);
        }
        if (TextUtils.isEmpty(sellerMarketCategory)) {
            sellerMarketCateogry.setError("Required Market Category");
            return;
        }
        else{
            sellerMarketCateogry.setError(null);
        }
        if (TextUtils.isEmpty(selRegStoreEmail)) {
            sellerStoreEmail.setError("Required Store Email");
            return;
        }
        else{
            sellerStoreEmail.setError(null);
        }
        if (selRegPassword.length() < 6) {
            sellerPassword.setError("Password must be 6 characters long");
            return;
        }
        else{
            sellerPassword.setError(null);
        }
        if (!selRegPersonalEmail.matches(emailPattern)) {
            sellerPersonalEmail.setError("Incorrect Email format");
        } else{
            sellerPersonalEmail.setError(null);
        }
        if (!selRegStoreEmail.matches(emailPattern)) {
            sellerStoreEmail.setError("Incorrect Email format");
        } else{
            sellerStoreEmail.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(selRegPersonalEmail).matches()) {
            sellerPersonalEmail.setError("Invalid Email");
        }
        else{
            sellerPersonalEmail.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(selRegStoreEmail).matches()) {
            sellerStoreEmail.setError("Invalid Email");
        }
        else{
            sellerStoreEmail.setError(null);
        }
        if (!selRegPassword.equals(selRegConfirmPassword)) {
            sellerConfirmPassword.setError("Password Doesn't match");
            return;
        }
        else{
            sellerConfirmPassword.setError(null);
        }
        if(regSelFName != null && regSelLName != null && regSelAddress != null && regSelAddress != null && selRegMobile != null &&
            selRegPersonalEmail != null && selRegStoreName != null && selRegStoreLocation != null && selRegStoreMarket != null &&
            selRegStoreEmail != null && selRegPassword != null && selRegConfirmPassword != null && sellerMarketCategory != null) {

            if(selRegStoreEmail.equals(null) && selRegPassword.equals(null) && selRegStoreEmail.equalsIgnoreCase("") && selRegPassword.equalsIgnoreCase("")){
                Toast.makeText(AdminRegisterSellerActivity.this,"Please review your credentials",Toast.LENGTH_SHORT).show();
            }else{
                createSeller(selRegStoreEmail, selRegPassword);
            }
        }
    }


    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private void createSeller(String email, String password){

        if(image_uri != null){
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    saveCustomerFirebase(email,password);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminRegisterSellerActivity.this, "Creation Failed",Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseCreation",e.getMessage());
                }
            });
        }else{
            Toast.makeText(AdminRegisterSellerActivity.this, "Cover photo needed",Toast.LENGTH_SHORT).show();
        }

    }

    private void saveCustomerFirebase(String email,String password){

        if(image_uri != null){

            String filePath = "store_cover/" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){

                                String timestamp = "" + System.currentTimeMillis();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("cover_photo", "" + downloadImageUri);
                                hashMap.put("email", "" + email);
                                hashMap.put("first_name", "" + regSelFName);
                                hashMap.put("last_name", "" + regSelLName);
                                hashMap.put("address", "" + regSelAddress);
                                hashMap.put("phoneNum", "" + selRegMobile);
                                hashMap.put("store_name", "" + selRegStoreName);
                                hashMap.put("store_location", "" + selRegStoreLocation);
                                hashMap.put("store_market", "" + selRegStoreMarket);
                                hashMap.put("store_category", "" + sellerMarketCategory);
                                hashMap.put("store_email", "" + selRegStoreEmail);
                                hashMap.put("store_password", "" + selRegPassword);
                                hashMap.put("accountType", "Seller");
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("online", "false");

                                DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AdminRegisterSellerActivity.this,"Registeration Complete",Toast.LENGTH_SHORT).show();
                                                clearFields();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminRegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        Log.e("FirebaseRegistration",e.getMessage());
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminRegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseRegistrationImg",e.getMessage());
                }
            });

        }else{
            Toast.makeText(AdminRegisterSellerActivity.this, "Cover photo needed",Toast.LENGTH_SHORT).show();
        }

    }
    private void clearFields(){
        sellerFName.getEditText().setText("");
        sellerLName.getEditText().setText("");
        sellerAddress.getEditText().setText("");
        sellerMobile.getEditText().setText("");
        sellerPersonalEmail.getEditText().setText("");
        sellerStoreName.getEditText().setText("");
        sellerStoreLocation.getEditText().setText("");
        sellerStoreMarket.getEditText().setText("");
        sellerStoreEmail.getEditText().setText("");
        sellerPassword.getEditText().setText("");
        sellerMarketCateogry.getEditText().setText("");
        sellerConfirmPassword.getEditText().setText("");
    }

}