package com.zeedroid.maparcade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Steve Dixon on 17/10/2017.
 */

public class PublishMapActivity extends AppCompatActivity {

    private Button publishButton;
    private EditText titleText, descriptionText;
    private NumberPicker difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_map);

        publishButton    = findViewById(R.id.publish);
        titleText        = findViewById(R.id.titleText);
        descriptionText  = findViewById(R.id.descriptionText);
        difficulty       = findViewById(R.id.difficulty);

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublishMapActivity.this,"Publish Button",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
