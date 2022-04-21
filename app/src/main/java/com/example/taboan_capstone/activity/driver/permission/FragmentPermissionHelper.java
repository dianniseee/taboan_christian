package com.example.taboan_capstone.activity.driver.permission;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import com.example.taboan_capstone.activity.driver.fragment.DeliveryFragment;

import java.util.Map;

public class FragmentPermissionHelper {

    public void permissionRequest(DeliveryFragment fr, FragmentPermissionInterface fs, String[] manifest){
        ActivityResultLauncher<String[]> multiplePermissionLauncher =
                fr.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> isGranted) -> {
                    boolean granted = true;
                    for (Map.Entry<String, Boolean> x : isGranted.entrySet())
                        if (!x.getValue()) granted = false;

                    if (granted){
                        fs.onGranted(true);
                    }
                });
        multiplePermissionLauncher.launch(manifest);
    }
}
