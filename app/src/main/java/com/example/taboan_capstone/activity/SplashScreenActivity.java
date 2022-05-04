package com.example.taboan_capstone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.admin.AdminDashboardActivity;
import com.example.taboan_capstone.activity.customer.CustomerHomeActivity;
import com.example.taboan_capstone.activity.driver.DriverDrawerActivity;
import com.example.taboan_capstone.activity.seller.SellerDashboardActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    private RoomDatabase roomDatabase;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();
        CurrentUserModel currentUserModel = roomDatabase.dbDao().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        if(roomDatabase.dbDao().checkIfCurrentUserExist() > 0){

            loginUser(currentUserModel.getEmail(),currentUserModel.getPassword());

        }else{
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginUser(String email,String password){

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(SplashScreenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(SplashScreenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }catch (FirebaseNetworkException e){
                                Toast.makeText(SplashScreenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(SplashScreenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            setUpStatus();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SplashScreenActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpStatus(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadUserInfo();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkUserType();
                            }
                        },500);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SplashScreenActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUserType(){

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren()){
                    String accountType = "" +ds.child("accountType").getValue();
                    if(accountType.equals("Admin")){
                        startActivity(new Intent(SplashScreenActivity.this, AdminDashboardActivity.class));
                        finish();
                    } else if (accountType.equals("Customer")){
                        startActivity(new Intent(SplashScreenActivity.this, CustomerHomeActivity.class));
                        finish();
                    } else if (accountType.equals("Seller")){
                        startActivity(new Intent(SplashScreenActivity.this, SellerDashboardActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashScreenActivity.this, DriverDrawerActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashScreenActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadUserInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){

                            String accountType = "" +ds.child("accountType").getValue().toString();

                            if(accountType.equals("Seller")){
                                Globals.currentUser = new CurrentUserModel(
                                        0,
                                        ds.child("uid").getValue().toString(),
                                        ds.child("first_name").getValue().toString(),
                                        ds.child("last_name").getValue().toString(),
                                        ds.child("email").getValue().toString(),
                                        ds.child("store_password").getValue().toString(),
                                        ds.child("phoneNum").getValue().toString(),
                                        ds.child("accountType").getValue().toString()
                                );
                            }else{
                                Globals.currentUser = new CurrentUserModel(
                                        0,
                                        ds.child("uid").getValue().toString(),
                                        ds.child("first_name").getValue().toString(),
                                        ds.child("last_name").getValue().toString(),
                                        ds.child("email").getValue().toString(),
                                        ds.child("password").getValue().toString(),
                                        ds.child("phoneNum").getValue().toString(),
                                        ds.child("accountType").getValue().toString()
                                );
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SplashScreenActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}