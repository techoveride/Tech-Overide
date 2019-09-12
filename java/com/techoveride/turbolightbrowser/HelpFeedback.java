package com.techoveride.turbolightbrowser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class HelpFeedback extends AppCompatActivity {

    private ImageButton btnGoBack;
    RadioButton feedback;
    RadioButton help;
    Button btnFeedback;
    EditText txtFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);
        setTitle("Help & Feedback");
        btnGoBack = findViewById(R.id.btnBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        help = findViewById(R.id.radio_help);
        feedback = findViewById(R.id.radio_feedback);
        txtFeedback = findViewById(R.id.txt_feedback);
        help.toggle();
        txtFeedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        btnFeedback = findViewById(R.id.btnFeedback);
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject;
                if (help.isActivated()) {
                    subject = "Help";
                } else {
                    subject = "Feedback";
                }
                String body = txtFeedback.getText().toString().trim();
                if (!body.isEmpty()) {
                    String mailto = "mailto:yaksonx2@gmail.com" +
                            "?cc=" + "" +
                            "&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(body);

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(mailto));

                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        //TODO: Handle case where no email app is available
                    }
                } else {
                    String show = "Feedback or Help cannot be empty";
                    customToast(show);
                }

            }
        });
    }

    private void customToast(String show) {
        Toast.makeText(this, show, Toast.LENGTH_SHORT).show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
