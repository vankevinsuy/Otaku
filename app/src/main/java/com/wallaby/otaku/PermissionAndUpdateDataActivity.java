package com.wallaby.otaku;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wallaby.otaku.internal_database.OtakuDatabase;

public class PermissionAndUpdateDataActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OtakuDatabase otakuDatabase = new OtakuDatabase(getApplicationContext());
        Cursor cursor = otakuDatabase.get_first_use();


        // permission d'accès à la carte sd
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    101);
            this.recreate();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            setContentView(R.layout.activity_main);


            // verifier si c'est la première utilisation
            if(cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    Log.d("cursor content", Integer.toString(cursor.getInt(0)));
                    otakuDatabase.verifyNewAdding();
                    //otakuDatabase.FirebaseSyncDataFromFirebase();
                }
            }
            else {
                otakuDatabase.initiate_first_use();
                otakuDatabase.verifyNewAdding();
                //otakuDatabase.FirebaseSyncDataFromFirebase();
            }


            BottomNavigationView navView = findViewById(R.id.nav_view);

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_scan,
                    R.id.navigation_anime,
                    R.id.navigation_movie)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

    }


    // method for closing the app
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.quitAppTittle)
                .setMessage(R.string.quitAppVerif)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionAndUpdateDataActivity.super.onBackPressed();
                        quit();
                        System.exit(0);
                    }
                }).create().show();
    }
    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }


}
