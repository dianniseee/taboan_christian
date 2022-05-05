package com.example.taboan_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.taboan_capstone.activity.LoginActivity;
import com.example.taboan_capstone.activity.customer.CustomerOrdersActivity;
import com.example.taboan_capstone.activity.customer.CustomerHomeActivity;
import com.example.taboan_capstone.activity.customer.CustomerProfileActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    RoomDatabase roomDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        roomDatabase = Room.databaseBuilder(this,RoomDatabase.class,"maindb").allowMainThreadQueries().build();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);



        switch (item.getItemId()){

            case R.id.customer_home:

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DrawerBaseActivity.this, CustomerHomeActivity.class));
                        overridePendingTransition(0,0);
                    }
                },300);

                break;

            case R.id.customer_profile:

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DrawerBaseActivity.this, CustomerProfileActivity.class));
                        overridePendingTransition(0,0);
                    }
                },300);

                break;

            case R.id.customer_order:

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DrawerBaseActivity.this, CustomerOrdersActivity.class));
                        overridePendingTransition(0,0);
                    }
                },300);

                break;

            case  R.id.customer_logout:
                overridePendingTransition(0,0);
                showCustomDialog();
                break;
        }

        return true;
    }

    protected void allocateActivtyTitle(String title){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerBaseActivity.this);
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
                        logoutCustomer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(DrawerBaseActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            finish();
                            startActivity(new Intent(DrawerBaseActivity.this, LoginActivity.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DrawerBaseActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}