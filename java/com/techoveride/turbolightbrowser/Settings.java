package com.techoveride.turbolightbrowser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import static com.techoveride.turbolightbrowser.MainActivity.*;

public class Settings extends AppCompatActivity {

    private ImageButton btnGoBack;
    private CheckBox jsCheckbox;
    private CheckBox zmCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Browser Settings");

        btnGoBack = findViewById(R.id.btnBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jsCheckbox = findViewById(R.id.checkBoxJS);
        jsStatus();
        jsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.i("Javascript is checked",""+isChecked);
                    javascriptStatus = true;
                }else{
                    javascriptStatus = false;
                    Log.i("Javascript not checked",""+isChecked);

                }
            }
        });
        zmCheckbox = findViewById(R.id.checkBoxZoom);
        zmStatus();
        zmCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    zoomControlStatus =true;
                }else{
                    zoomControlStatus = false;
                }
            }
        });

    }

    private void zmStatus() {
        if(zoomControlStatus){
            Log.i("Zoom Mode stat",""+zoomControlStatus);
            zmCheckbox.setChecked(true);
        }else{
            zmCheckbox.setChecked(false);
            Log.i("Zoom Mode stat",""+zoomControlStatus);

        }
    }

    private void jsStatus() {
        if(javascriptStatus){
            Log.i("Javascript stat",""+javascriptStatus);
            jsCheckbox.setChecked(true);
        }else{
            jsCheckbox.setChecked(false);
            Log.i("Javascript stat",""+javascriptStatus);

        }
    }
}
