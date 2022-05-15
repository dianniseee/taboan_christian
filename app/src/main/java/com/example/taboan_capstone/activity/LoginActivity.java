package com.example.taboan_capstone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cysion.wedialog.WeDialog;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.admin.AdminDashboardActivity;
import com.example.taboan_capstone.activity.customer.CustomerHomeActivity;
import com.example.taboan_capstone.activity.customer.CustomerRegisterActivity;
import com.example.taboan_capstone.activity.driver.DriverDrawerActivity;
import com.example.taboan_capstone.activity.seller.SellerDashboardActivity;
import com.example.taboan_capstone.models.CurrentUserModel;
import com.example.taboan_capstone.models.DriverModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLogin,emailPassword;
    private Button btnLogin;
    private TextView tvSingup;

    private String email,password;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    //Firebase
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        emailLogin = findViewById(R.id.emailLogin);
        emailPassword = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSingup = findViewById(R.id.tv_register);

        firebaseAuth = FirebaseAuth.getInstance();

        createUI();
    }

    private void createUI(){

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeDialog.INSTANCE.loading(LoginActivity.this);
                loginUser();
            }
        });

        tvSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CustomerRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(){
        email = emailLogin.getEditText().getText().toString().trim();
        password = emailPassword.getEditText().getText().toString().trim();

       if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           Toast.makeText(LoginActivity.this,"Invalid email format", Toast.LENGTH_SHORT).show();
       }
       else if(TextUtils.isEmpty(email)){
           Toast.makeText(LoginActivity.this,"Empty Email", Toast.LENGTH_SHORT).show();
       }else{

           if(TextUtils.isEmpty(password)){
               Toast.makeText(LoginActivity.this,"Empty Password", Toast.LENGTH_SHORT).show();

           }else{
               firebaseAuth.signInWithEmailAndPassword(email,password)
                       .addOnCompleteListener(this, task -> {
                           if(!task.isSuccessful()){
                               try{
                                   throw task.getException();
                               }catch (FirebaseAuthInvalidUserException e){
                                   emailLogin.setError("Invalid email");
                                   emailLogin.requestFocus();
                                   WeDialog.INSTANCE.dismiss();
                               }catch (FirebaseAuthInvalidCredentialsException e){
                                   emailPassword.setError("Invalid password");
                                   emailPassword.requestFocus();
                                   WeDialog.INSTANCE.dismiss();
                               }catch (FirebaseNetworkException e){
                                   Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   WeDialog.INSTANCE.dismiss();
                               }catch (Exception e){
                                   Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   WeDialog.INSTANCE.dismiss();
                               }
                           }else{
                               new Handler(Looper.getMainLooper()).postDelayed(() -> setUpStatus(),500);
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d("signInEmailAndPassword",e.getMessage());
                       WeDialog.INSTANCE.dismiss();
                   }
               });
           }
       }

    }

    private void setUpStatus(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> checkUserType())
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    WeDialog.INSTANCE.dismiss();
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
                        loadAdminInfo();
                    }
                    else if (accountType.equals("Customer")){
                        loadCustomerInfo();
                    }
                    else if (accountType.equals("Seller")){
                        loadSellerInfo();
                    } else{
                        loadDriverInfo();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                WeDialog.INSTANCE.dismiss();
            }
        });
    }

    private void loadCustomerInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
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

                        emailLogin.getEditText().getText().clear();
                        emailPassword.getEditText().getText().clear();
                        WeDialog.INSTANCE.dismiss();
                        startActivity(new Intent(LoginActivity.this, CustomerHomeActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSellerInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            Globals.currentUser = new CurrentUserModel(
                                    0,
                                    ds.child("uid").getValue().toString(),
                                    ds.child("first_name").getValue().toString(),
                                    ds.child("last_name").getValue().toString(),
                                    ds.child("store_email").getValue().toString(),
                                    ds.child("store_password").getValue().toString(),
                                    ds.child("phoneNum").getValue().toString(),
                                    ds.child("accountType").getValue().toString()
                            );
                        }

                        emailLogin.getEditText().getText().clear();
                        emailPassword.getEditText().getText().clear();
                        WeDialog.INSTANCE.dismiss();

                        startActivity(new Intent(LoginActivity.this, SellerDashboardActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAdminInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
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

                        emailLogin.getEditText().getText().clear();
                        emailPassword.getEditText().getText().clear();
                        // showAdminLoginDialog();
                        WeDialog.INSTANCE.dismiss();

                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDriverInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
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

                            Globals.currentDriver = ds.getValue(DriverModel.class);
                        }

                        emailLogin.getEditText().getText().clear();
                        emailPassword.getEditText().getText().clear();
                        WeDialog.INSTANCE.dismiss();

                        startActivity(new Intent(LoginActivity.this, DriverDrawerActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAdminLoginDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.seller_login_dialog,null);
        TextInputLayout adminEmail = view.findViewById(R.id.adminEmail);
        TextInputLayout adminPassword = view.findViewById(R.id.adminPassword);
        Button adminLogin = (Button) view.findViewById(R.id.btn_admin_login);

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        //nothing do here
    }

}