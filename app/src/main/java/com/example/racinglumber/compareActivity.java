package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class compareActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    private BottomNavigationView bottomNavigationView;
//        private static String returnString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_save_button);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                startActivity(new Intent(compareActivity.this, MainActivity.class));
                break;
            case R.id.bottom_nav_graph_button:
                startActivity(new Intent(compareActivity.this, graphActivity.class));
                break;

            case R.id.bottom_nav_compare_button:
                break;

            case R.id.bottom_nav_save_button:
                startActivity(new Intent(compareActivity.this, fileManageActivity.class));
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }
}
