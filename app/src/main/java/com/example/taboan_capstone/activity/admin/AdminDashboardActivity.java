package com.example.taboan_capstone.activity.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.LoginActivity;
import com.example.taboan_capstone.activity.SplashScreenActivity;
import com.example.taboan_capstone.activity.customer.CustomerHomeActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageView adminPortal,adminDriver,adminRegSeller;
    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminPortal = findViewById(R.id.iv_admin_portal);
        adminDriver = findViewById(R.id.iv_admin_driver_status);
        adminRegSeller = findViewById(R.id.iv_admin_reg_seller);

        firebaseAuth = FirebaseAuth.getInstance();
        roomDatabase = Room.databaseBuilder(getApplicationContext(), RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        createUI();
    }

    private void createUI(){

        adminPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        adminDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminRegisterDriverActivity.class);
                startActivity(intent);
            }
        });

        adminRegSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminRegisterSellerActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        showCustomDialog();
    }

    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboardActivity.this);
        builder.setMessage("Do you want to logout?")
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Confirm</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setUpStatus();
                    }
                }).setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpStatus(){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        logoutAdmin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(AdminDashboardActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutAdmin(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        String accountType = "" +datasnapshot.child("accountType").getValue();
                        if(accountType.equals("Admin")){
                            firebaseAuth.signOut();
                            roomDatabase.clearAllTables();

                           new Handler(Looper.getMainLooper()).postDelayed(() -> {
                               startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
                               finish();
                           },300);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}