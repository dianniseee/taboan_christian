package com.example.taboan_capstone.activity.seller;

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
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class SellerAddProductActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE_1 = 200;
    private static final int CAMERA_REQUEST_CODE_2 = 300;

    private static final int STORAGE_REQUEST_CODE = 400;

    private static final int IMAGE_PICK_GALLERY_CODE_1 = 400;
    private static final int IMAGE_PICK_GALLERY_CODE_2 = 500;

    private static final int IMAGE_PICK_CAMERA_CODE_1 = 600;
    private static final int IMAGE_PICK_CAMERA_CODE_2 = 700;

    private static final int IMAGE_FIRST_PIC_CODE = 100;
    private static final int IMAGE_SECOND_PIC_CODE = 200;

    private Uri image_uri1,image_uri2;

    private boolean isFromFirstImg;

    private String[] cameraPermissions;
    private String[] storagePermission;

    private ImageView back_add,productPhoto1,productPhoto2;
    private TextInputLayout productName,productDescription,priceCategory,productPrice,availability;
    private TextInputEditText pricateCategory_onTouch,prodAvailability_onTouch;
    private Button reg_product;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    private String getImage1 = "";
    private String getImage2 = "";

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        back_add = findViewById(R.id.back_add);
        productPhoto1 = findViewById(R.id.iv_productPhoto1);
        productPhoto2 = findViewById(R.id.iv_productPhoto2);
        productName = findViewById(R.id.ti_productName);
        productDescription = findViewById(R.id.ti_productDescription);
        priceCategory = findViewById(R.id.ti_productCategory);
        productPrice = findViewById(R.id.ti_productPrice);
        availability = findViewById(R.id.ti_productAvailability);
        pricateCategory_onTouch = findViewById(R.id.te_productCategory);
        reg_product = findViewById(R.id.btn_register_product);
        prodAvailability_onTouch = findViewById(R.id.te_productAvailability);

        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        productPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromFirstImg = true;
                showImagePickDialog();
            }
        });

        productPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromFirstImg = false;
                showImagePickDialog();
            }
        });

        pricateCategory_onTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryMarketPlace();
            }
        });

        reg_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputProduct();
            }
        });

        back_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        prodAvailability_onTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productAvailability();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case CAMERA_REQUEST_CODE_1:{
                if(grantResults.length>0){
                    boolean cameraAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCameraOne();
                    }
                    else{
                        requestCameraPermission();
                        requestStoragePermission();
                    }
                }
            }
            case CAMERA_REQUEST_CODE_2:{
                if(grantResults.length>0){
                    boolean cameraAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCameraSecond();
                    }
                    else{
                        requestCameraPermission();
                        requestStoragePermission();
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
                        if(requestCode == IMAGE_PICK_GALLERY_CODE_1){
                            pickFromGalleryOne();
                        }else if(requestCode == IMAGE_PICK_GALLERY_CODE_2){
                            pickFromGalleryTwo();
                        }
                    }
                    else{
                        requestStoragePermission();
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

            if(isFromFirstImg){
                if(requestCode == IMAGE_PICK_GALLERY_CODE_1){
                    image_uri1 = data.getData();
                    image_uri1 = Uri.parse(data.getData().toString());

                    Glide.with(this).load(image_uri1).into(productPhoto1);
                }
                if(requestCode == IMAGE_PICK_CAMERA_CODE_1){
                    image_uri1 = data.getData();
                    image_uri1 = Uri.parse(data.getData().toString());

                    Glide.with(this).load(image_uri1).into(productPhoto1);
                }
            }else{
                if(requestCode == IMAGE_PICK_GALLERY_CODE_2){
                    image_uri2 = data.getData();
                    image_uri2 = Uri.parse(data.getData().toString());

                    Glide.with(this).load(image_uri2).into(productPhoto2);
                }
                else if(requestCode == IMAGE_PICK_CAMERA_CODE_2){
                    image_uri2 = data.getData();
                    image_uri2 = Uri.parse(data.getData().toString());

                    Glide.with(this).load(image_uri2).into(productPhoto2);
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void categoryMarketPlace() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Markets")
                .setItems(Constants.Companion.getPRICE_CATEGORY(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String category = Constants.Companion.getPRICE_CATEGORY()[i];
                        priceCategory.getEditText().setText(category);
                    }
                }).show();

    }

    private void productAvailability() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Availability")
                .setItems(Constants.Companion.getPROD_AVAILABILITY(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String category = Constants.Companion.getPROD_AVAILABILITY()[i];
                        availability.getEditText().setText(category);
                    }
                }).show();

    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {

                        if (position == 0) {
                            if (checkCameraPermission()) {

                                if(isFromFirstImg){
                                    pickFromCameraOne();
                                }else{
                                    pickFromCameraSecond();
                                }
                            }
                            else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                if(isFromFirstImg){
                                    pickFromGalleryOne();
                                }else{
                                    pickFromGalleryTwo();
                                }
                            }
                            else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromCameraOne() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri1);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE_1);
    }

    private void pickFromCameraSecond() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri2 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri2);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE_2);
    }

    private void pickFromGalleryOne() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE_1);
    }

    private void pickFromGalleryTwo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE_2);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(SellerAddProductActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(SellerAddProductActivity.this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(SellerAddProductActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(SellerAddProductActivity.this, cameraPermissions, CAMERA_REQUEST_CODE_1);
        ActivityCompat.requestPermissions(SellerAddProductActivity.this, cameraPermissions, CAMERA_REQUEST_CODE_2);
    }

    String prodName,prodDescription,priCategory,prodPrice,avail;
    private void inputProduct(){
        prodName = Objects.requireNonNull(productName.getEditText()).getText().toString().trim();
        prodDescription = Objects.requireNonNull(productDescription.getEditText()).getText().toString().trim();
        priCategory = Objects.requireNonNull(priceCategory.getEditText()).getText().toString().trim();
        prodPrice = Objects.requireNonNull(productPrice.getEditText()).getText().toString().trim();
        avail = Objects.requireNonNull(availability.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(prodName)) {
            productName.setError("Required Product Name");
            return;
        }
        else{
            productName.setError(null);
        }
        if (TextUtils.isEmpty(prodDescription)) {
            productDescription.setError("Required Description");
            return;
        }
        else{
            productDescription.setError(null);
        }
        if (TextUtils.isEmpty(priCategory)) {
            priceCategory.setError("Required Price Category");
            return;
        }
        else{
            priceCategory.setError(null);
        }
        if (TextUtils.isEmpty(prodPrice)) {
            productPrice.setError("Required Product Price");
            return;
        }
        else{
            productPrice.setError(null);
        }
        if (TextUtils.isEmpty(avail)) {
            availability.setError("Required Availability");
            return;
        }
        else{
            availability.setError(null);
        }

        //prodName,prodDescription,priCategory,prodPrice,avail;
        if(prodName != null && prodDescription != null && priCategory != null && prodPrice != null && avail != null){
            saveProduct();
        }
    }

    private void saveProduct(){

        String filePathName = "product_images/";
        final String timestamp = ""+System.currentTimeMillis();

        StorageReference storageReference = FirebaseStorage.getInstance(Globals.INSTANCE.getFirebaseStorange()).getReference(filePathName + +System.currentTimeMillis());
        storageReference.putFile(image_uri1)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadImage1 = uriTask.getResult();
                    if(uriTask.isSuccessful()){



                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StorageReference storageReference1 = FirebaseStorage.getInstance(Globals.INSTANCE.getFirebaseStorange()).getReference(filePathName + +System.currentTimeMillis());
                                storageReference1.putFile(image_uri2).addOnSuccessListener(taskSnapshot1 -> {
                                    Task<Uri> uriTask1 = taskSnapshot1.getStorage().getDownloadUrl();
                                    while(!uriTask1.isSuccessful());
                                    Uri downloadImage2 = uriTask1.getResult();

                                    if(uriTask1.isSuccessful()){

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("prod_id","" +timestamp);
                                        hashMap.put("prod_seller", ""+ firebaseAuth.getUid());
                                        hashMap.put("prod_name", ""+prodName);
                                        hashMap.put("prod_desc",""+prodDescription);
                                        hashMap.put("prod_image1", ""+downloadImage1);
                                        hashMap.put("prod_image2", ""+downloadImage2);
                                        hashMap.put("prod_category",""+priCategory);
                                        hashMap.put("prod_price",""+prodPrice);
                                        hashMap.put("prod_avail",""+avail);

                    DatabaseReference ref2 = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                    ref2.child(firebaseAuth.getUid()).child("Product").child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SellerAddProductActivity.this, "ProductAdded", Toast.LENGTH_SHORT).show();
                                    clearData();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SellerAddProductActivity.this, "AddProduct_error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(SellerAddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

                            }
                        },300);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SellerAddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    private void clearData(){
        productName.getEditText().setText("");
        productDescription.getEditText().setText("");
        priceCategory.getEditText().setText("");
        productPrice.getEditText().setText("");
        availability.getEditText().setText("");
        productPhoto1.setImageResource(R.drawable.ic_cover_add);
        productPhoto2.setImageResource(R.drawable.ic_cover_add);
        image_uri1 = null;
        image_uri2 = null;
    }
}