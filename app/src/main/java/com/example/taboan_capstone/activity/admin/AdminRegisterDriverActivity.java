package com.example.taboan_capstone.activity.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.customer.CustomerRegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class AdminRegisterDriverActivity extends AppCompatActivity {

    private TextInputLayout ti_FirstName, ti_LastName, ti_Address, ti_Mobile, ti_Email, ti_Password, ti_Confirm_Password;
    private Button btnSignUp;
    private ImageView back;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_driver);

        ti_FirstName = findViewById(R.id.driver_ti_FirstName);
        ti_LastName = findViewById(R.id.driver_ti_LastName);
        ti_Address = findViewById(R.id.driver_ti_Address);
        ti_Mobile = findViewById(R.id.driver_ti_Mobile);
        ti_Email = findViewById(R.id.driver_ti_Email);
        ti_Password = findViewById(R.id.driver_ti_Password);
        ti_Confirm_Password = findViewById(R.id.driver_ti_Confirm_Password);
        btnSignUp = findViewById(R.id.driver_btn_SingUp);
        back = findViewById(R.id.back);

        firebaseAuth = FirebaseAuth.getInstance();

        creatUI();
    }

    private void creatUI(){

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private String regFName,regLName,regAddress,regMobile,
            regConfirmPassword,userCity,userCountry,userPostal;

    private void registerUser(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String regEmail,regPassword;

        regFName = Objects.requireNonNull(ti_FirstName.getEditText()).getText().toString().trim();
        regLName = Objects.requireNonNull(ti_LastName.getEditText()).getText().toString().trim();
        regAddress = Objects.requireNonNull(ti_Address.getEditText()).getText().toString().trim();
        regMobile = Objects.requireNonNull(ti_Mobile.getEditText()).getText().toString().trim();
        regEmail = Objects.requireNonNull(ti_Email.getEditText()).getText().toString().trim();
        regPassword = Objects.requireNonNull(ti_Password.getEditText()).getText().toString().trim();
        regConfirmPassword = Objects.requireNonNull(ti_Confirm_Password.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(regFName)) {
            ti_FirstName.setError("Required First Name");
            return;
        }
        else{
            ti_FirstName.setError(null);
        }
        if (TextUtils.isEmpty(regLName)) {
            ti_LastName.setError("Required your Last Name");
            return;
        }else{
            ti_LastName.setError(null);
        }
        if (TextUtils.isEmpty(regAddress)) {
            ti_Address.setError("Required address");
            return;
        }
        else{
            ti_Address.setError(null);
        }

        if (TextUtils.isEmpty(regMobile)) {
            ti_Mobile.setError("Required Mobile Number");
            return;
        }
        else{
            ti_Mobile.setError(null);
        }
        if (TextUtils.isEmpty(regEmail)) {
            ti_Email.setError("Required email address");
        }else{
            ti_Email.setError(null);
        }
        if (TextUtils.isEmpty(regPassword)) {
            ti_Password.setError("Required your password");
            return;
        }
        else{
            ti_Password.setError(null);
        }
        if (TextUtils.isEmpty(regConfirmPassword)) {
            ti_Confirm_Password.setError("Required your password");
            return;
        }
        else{
            ti_Confirm_Password.setError(null);
        }
        if (regPassword.length() < 6) {
            ti_Password.setError("Password must be 6 characters long");
            return;
        }
        else{
            ti_Password.setError(null);
        }
        if (!regEmail.matches(emailPattern)) {
            ti_Email.setError("Incorrect Email format");
        } else{
            ti_Email.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
            ti_Email.setError("Invalid Email");
        }
        else{
            ti_Email.setError(null);
        }
        if (!regPassword.equals(regConfirmPassword)) {
            ti_Confirm_Password.setError("Password Doesn't match");
            return;
        }
        else{
            ti_Confirm_Password.setError(null);
        }
        if(regFName != null && regLName != null && regAddress != null && regMobile != null && regPassword.equalsIgnoreCase(regConfirmPassword)){

            if(regEmail.equals(null) && regPassword.equals(null) && regEmail.equalsIgnoreCase("") && regPassword.equalsIgnoreCase("")){
                Toast.makeText(AdminRegisterDriverActivity.this,"Please review your credentials",Toast.LENGTH_SHORT).show();
            }else{
                createDriver(regEmail,regPassword);
            }
        }
    }
    private void createDriver(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                saveDriverFirebase(email,password);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminRegisterDriverActivity.this, "Creation Failed",Toast.LENGTH_SHORT).show();
                Log.e("FirebaseCreation",e.getMessage());
            }
        });
    }

    private void saveDriverFirebase(String email,String password){
        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("email", "" + email);
        hashMap.put("first_name", "" + regFName);
        hashMap.put("last_name", "" + regLName);
        hashMap.put("phoneNum", "" + regMobile);
        hashMap.put("address", "" + regAddress);
        hashMap.put("password", "" + password);
        hashMap.put("latitude", "" );
        hashMap.put("longitude", "" );
        hashMap.put("accountType", "Driver");
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("online", "false");
        hashMap.put("availStat", "Offline");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).setValue(hashMap).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminRegisterDriverActivity.this,"Registeration Complete",Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminRegisterDriverActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                Log.e("FirebaseRegistration",e.getMessage());
            }
        });
    }
    private void clearFields(){
        ti_FirstName.getEditText().setText("");
        ti_LastName.getEditText().setText("");
        ti_Address.getEditText().setText("");
        ti_Mobile.getEditText().setText("");
        ti_Email.getEditText().setText("");
        ti_Password.getEditText().setText("");
        ti_Confirm_Password.getEditText().setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}