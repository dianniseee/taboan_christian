package com.example.taboan_capstone.activity.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.taboan_capstone.DrawerBaseActivity;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.LoginActivity;
import com.example.taboan_capstone.activity.driver.fragment.HistoryFragment;
import com.example.taboan_capstone.activity.driver.fragment.PortalFragment;
import com.example.taboan_capstone.activity.driver.fragment.ProfileFragment;
import com.example.taboan_capstone.activity.driver.fragment.StatusFragment;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.example.taboan_capstone.models.DriverModel;
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
import java.util.List;

public class DriverDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_drawer);
        drawerLayout = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.dToolbar);
        navigationView = findViewById(R.id.navigation_view);

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new PortalFragment());

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new PortalFragment()).commit();
            navigationView.setCheckedItem(R.id.driver_portal);
        }

        roomDatabase = Room.databaseBuilder(getApplicationContext(),RoomDatabase.class,"maindb").allowMainThreadQueries().build();

        if(roomDatabase.dbDao().checkIfCurrentUserExist() != 0){
            loadDriver();
        }else{
            insertToDao();
        }

    }

    private void loadDriver(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Globals.currentDriver = ds.getValue(DriverModel.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverDrawerActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()){
            case R.id.driver_portal:
                fragment = new PortalFragment();
                loadFragment(fragment);
                break;

            case R.id.driver_status:
                fragment = new StatusFragment();
                loadFragment(fragment);
                break;

            case R.id.driver_profile:
                fragment = new ProfileFragment();
                loadFragment(fragment);
                break;

            case R.id.driver_history:
                fragment = new HistoryFragment();
                loadFragment(fragment);
                break;

            case R.id.driver_logout:
                showCustomDialog();
                break;

            default:
                return true;
        }

        return true;
    }

    private void loadFragment(Fragment fragment){
        drawerLayout.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer,fragment).commit();
                fragmentTransaction.addToBackStack(null);
            }
        },300);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverDrawerActivity.this);
        AlertDialog alert = builder.create();
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
                alert.dismiss();
            }
        });


        alert.show();
    }

    private void makeMeOffline(){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Sellers");
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
                        Toast.makeText(DriverDrawerActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    if(accountType.equals("Driver")){
                        firebaseAuth.signOut();
                        roomDatabase.clearAllTables();
                        startActivity(new Intent(DriverDrawerActivity.this, LoginActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverDrawerActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}