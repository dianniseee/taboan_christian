package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cysion.wedialog.WeDialog;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.LoginActivity;
import com.example.taboan_capstone.activity.driver.DriverDrawerActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SellerDashboardActivity extends AppCompatActivity {

    private ImageView sellerPortal,sellerAddProduct,sellerMenu,sellerHistory;
    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        sellerPortal = findViewById(R.id.iv_seller_portal);
        sellerAddProduct = findViewById(R.id.iv_seller_add_product);
        sellerMenu = findViewById(R.id.iv_seller_menu);
        sellerHistory = findViewById(R.id.iv_seller_history);

        firebaseAuth = FirebaseAuth.getInstance();

        sellerPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDashboardActivity.this, SellerPortalActivity.class);
                startActivity(intent);
            }
        });

        sellerAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDashboardActivity.this, SellerAddProductActivity.class);
                startActivity(intent);
            }
        });

        sellerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDashboardActivity.this, SellerMenuActivity.class);
                startActivity(intent);
            }
        });

        sellerHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDashboardActivity.this, SellerHistoryActivity.class);
                startActivity(intent);
            }
        });

        roomDatabase = Room.databaseBuilder(getApplicationContext(), RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        if(roomDatabase.dbDao().checkIfCurrentUserExist() != 0){
            //nothing to be call here
         }else{
            insertToDao();
        }
    }

    @Override
    public void onBackPressed() {
        showCustomDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SellerDashboardActivity.this);
        builder.setMessage("Do you want to logout?")
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Confirm</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        makeMeOffline();
                    }
                }).setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeMeOffline(){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellerDashboardActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkUserType(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren()){
                    String accountType = "" +ds.child("accountType").getValue();

                    if(accountType.equals("Seller")){
                        firebaseAuth.signOut();
                        roomDatabase.clearAllTables();
                        startActivity(new Intent(SellerDashboardActivity.this, LoginActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerDashboardActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertToDao(){

        roomDatabase.dbDao().insertCurrentUser(new CurrentUserModel(
                0,
                Globals.INSTANCE.getCurrentUser().getUid(),
                Globals.INSTANCE.getCurrentUser().getFirst_name(),
                Globals.INSTANCE.getCurrentUser().getLast_name(),
                Globals.INSTANCE.getCurrentUser().getEmail(),
                Globals.INSTANCE.getCurrentUser().getPassword(),
                Globals.INSTANCE.getCurrentUser().getMobile_phone(),
                Globals.INSTANCE.getCurrentUser().getUser_type()));
    }
}