package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class fileManageActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    private static String returnString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Button loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_save_button);
    }

    //////////////////////////File Access Functions//////////////////////////

    // Request code for creating a PDF document.
  ////  private static final int CREATE_FILE = 1;
    private void createFile() { //Uri pickerInitialUri
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "DUMMY.txt");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, fileSave);//CREATE_FILE
    }

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;
    private void openFile() { //Uri pickerInitialUri
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/txt");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    private static final int fileSave = 101; //todo replace with the standard define
    private static final int fileLoad = 2; //todo replace with the standard define

    //todo debug note, below runs after load button

    //todo clean this code up, these branches are terrible
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri uri;

        if ((requestCode == fileLoad) && (resultCode == Activity.RESULT_OK))
        {
            // The result data contains a URI for the document or directory that the user selected
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                returnString = uri.toString();
            }
        }
        else if ((requestCode == fileSave) && (resultCode == Activity.RESULT_OK))
        {
            // The result data contains a URI for the document or directory that the user created
            if (resultData != null) {
                // Perform operations on the document using its URI.
                writeInFile(resultData.getData(), "THIS TEXT IS NOT NULL.  IT EXISTS");
            }
        }
        else
        {
            //do nothing
        }
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //////////////////////////User Interface Functions//////////////////////////

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.saveButton)
        {
            //Save button clicked
            this.createFile();
        }
        else
        {
            //Load button clicked
            this.openFile();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                startActivity(new Intent(fileManageActivity.this, MainActivity.class));
                break;
            case R.id.bottom_nav_graph_button:
                startActivity(new Intent(fileManageActivity.this, graphActivity.class));
                break;
            case R.id.bottom_nav_save_button:
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }
}