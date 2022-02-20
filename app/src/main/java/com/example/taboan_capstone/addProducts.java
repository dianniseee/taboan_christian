package com.example.taboan_capstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class addProducts extends Fragment implements AdapterView.OnItemSelectedListener{

    String spName;
    TextView addPhoto;
    EditText prodName, prodDesc, prodPrice, quantity;
    Spinner category;
    ImageView selectedImage;
    Button addProd;
    private static final int PICK_IMAGE = 111;
    Bitmap bitmap;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";

    public addProducts() {
        // Required empty public constructor
    }


    public static addProducts newInstance(String param1, String param2) {
        addProducts fragment = new addProducts();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_add_products, container, false);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);

       addPhoto = v.findViewById(R.id.addPhoto);
       selectedImage = v.findViewById(R.id.selectedImage);
       prodName = v.findViewById(R.id.prodName);
       prodDesc = v.findViewById(R.id.prodDesc);
       prodPrice = v.findViewById(R.id.price);
       category = v.findViewById(R.id.category);
       quantity = v.findViewById(R.id.qty);
       addProd = v.findViewById(R.id.btnAdd);




       //method calls
        selectImage();
        spinnerEvents();
        setAddProd();

       return v;
    }

    public void setAddProd() {
        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(spName);
            }
        });
    }

    public void spinnerEvents(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (getActivity(),R.array.courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            Uri imagePath = data.getData();

            try{
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);

                selectedImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void selectImage(){
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,"Select Image"), PICK_IMAGE);
            }
        });

    }

    public void addItem(String userAdmin){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        stringRequest = new StringRequest(Request.Method.POST, "https://capierap.online/insertProd.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(),"Product Added!",Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();

                map.put("userAdmin", userAdmin);
                map.put("prodName", prodName.getText().toString());
                map.put("prodDesc",prodDesc.getText().toString());
                map.put("prodPrice",prodPrice.getText().toString());
                map.put("prodCat",category.getSelectedItem().toString());
                map.put("prodQuantity",quantity.getText().toString());
                map.put("prodImage",imageString);

                return map;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}