package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.DrawerBaseActivity;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.LoginActivity;
import com.example.taboan_capstone.activity.SplashScreenActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.databinding.ActivityCustomerHomeBinding;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.example.taboan_capstone.models.CustomerCartModel;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlinx.coroutines.GlobalScope;

public class CustomerHomeActivity extends DrawerBaseActivity {

    private ActivityCustomerHomeBinding binding;
    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;

    private RelativeLayout agdaoBase,bangkeBase,panacanBase,piapiBase,sasaBase,tibungcoBase;
    private TextView welcomeName;

    private int selectIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerHomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        allocateActivtyTitle("Home");

        agdaoBase = binding.getRoot().findViewById(R.id.cat_Agdao_base);
        bangkeBase = binding.getRoot().findViewById(R.id.cat_bangke_base);
        panacanBase = binding.getRoot().findViewById(R.id.cat_panacan_base);
        piapiBase = binding.getRoot().findViewById(R.id.cat_piapi_base);
        sasaBase = binding.getRoot().findViewById(R.id.cat_sasa_base);
        tibungcoBase = binding.getRoot().findViewById(R.id.cat_tibungco_base);
        welcomeName = binding.getRoot().findViewById(R.id.tv_welcome_name);

        firebaseAuth = FirebaseAuth.getInstance();
        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        if(roomDatabase.dbDao().checkIfCurrentUserExist() != 0){
            //insert to dao when dao is empty
            welcomeName.setText(Globals.INSTANCE.getCurrentUser().getFirst_name() + " " + Globals.INSTANCE.getCurrentUser().getLast_name());
        }else{
            insertToDao();
        }

        agdaoBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Agdao";
                notifyDialog(market);
            }
        });

        bangkeBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Bangkerohan";
                notifyDialog(market);
            }
        });

        panacanBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Panacan";
                notifyDialog(market);
            }
        });

        piapiBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Piapi";
                notifyDialog(market);
            }
        });

        sasaBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Sasa";
                notifyDialog(market);
            }
        });

        tibungcoBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String market = "Tibungco";
                notifyDialog(market);
            }
        });

    }

    private void notifyDialog(String market){
        List<String> cartMarket = roomDatabase.dbDao().getDistinctMarketCart();

        if(cartMarket.size() > 0 ){

            for(int i = 0; i < cartMarket.size(); i++){
                String marketCart = cartMarket.get(i);

                if(!market.equals(marketCart) && roomDatabase.dbDao().checkIfCustomerCartExist() > 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomeActivity.this);

                    builder.setMessage("You have already selected different market. If you continue your cart and selection will be removed. ")
                            .setCancelable(false)
                            .setPositiveButton(Html.fromHtml("<font color='#000000'>Confirm</font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    roomDatabase.dbDao().clearCustomerCart();
                                    Intent intent = new Intent(CustomerHomeActivity.this, CustomerDashboardActivity.class);
                                    intent.putExtra("market",market);
                                    startActivity(intent);
                                }
                            }).setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }else if(market.equals(marketCart) && roomDatabase.dbDao().checkIfCustomerCartExist() >= 0){
                    Intent intent = new Intent(CustomerHomeActivity.this, CustomerDashboardActivity.class);
                    intent.putExtra("market",market);
                    startActivity(intent);
                }
            }

        }else{
            Intent intent = new Intent(CustomerHomeActivity.this, CustomerDashboardActivity.class);
            intent.putExtra("market",market);
            startActivity(intent);
        }
    }

    private void checkLatLng(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");

    }

    @Override
    public void onBackPressed() {
        showCustomDialog();
    }

    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomeActivity.this);

        builder.setMessage("Do you want to logout?")
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Confirm</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setUpStatus();
                        startActivity(new Intent(CustomerHomeActivity.this, LoginActivity.class));
                    }
                }).setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nothing to do here : clicking by it will dimis the dialog
                //put builder below to show the dialog
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
                        logoutCustomer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(CustomerHomeActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutCustomer(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        String accountType = "" +datasnapshot.child("accountType").getValue();
                        if(accountType.equals("Customer")){

                            firebaseAuth.signOut();
                            roomDatabase.clearAllTables();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

        welcomeName.setText(Globals.INSTANCE.getCurrentUser().getFirst_name() + " " + Globals.INSTANCE.getCurrentUser().getLast_name());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}