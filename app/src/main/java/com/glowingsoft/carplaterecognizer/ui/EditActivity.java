package com.glowingsoft.carplaterecognizer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.glowingsoft.carplaterecognizer.R;

/* loaded from: classes.dex */
public class EditActivity extends AppCompatActivity {
    ImageButton back;
    EditText plate;
    EditText region;
    Button saveResult;
    EditText vihical;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();
        ImageButton imageButton = (ImageButton) findViewById(R.id.back_btn_type);
        this.back = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.glowingsoft.carplaterecognizer.ui.EditActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EditActivity.this.finish();
            }
        });
        this.plate = (EditText) findViewById(R.id.car_plate_edit);
        this.region = (EditText) findViewById(R.id.region_code_edit);
        this.vihical = (EditText) findViewById(R.id.vihical_type_edit);
        Intent intent = getIntent();
        this.plate.setText(intent.getStringExtra("car_plate"));
        this.region.setText(intent.getStringExtra("region_code"));
        this.vihical.setText(intent.getStringExtra("car_type"));
        Button button = (Button) findViewById(R.id.save_btn);
        this.saveResult = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.glowingsoft.carplaterecognizer.ui.EditActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EditActivity.this.saveDate();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDate() {
        String obj = this.plate.getText().toString();
        String obj2 = this.region.getText().toString();
        String obj3 = this.vihical.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("car_plate", obj);
        intent.putExtra("region_code", obj2);
        intent.putExtra("car_type", obj3);
        setResult(-1, intent);
        finish();
    }
}
