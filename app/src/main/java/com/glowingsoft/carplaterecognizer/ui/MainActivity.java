package com.glowingsoft.carplaterecognizer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;
import cz.msebera.android.httpclient.Header;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity implements IPickResult, View.OnClickListener {
    Context context;
    Date date;
    DateFormat df;
    ImageButton editResult;
    ImageView emptyImage;
    FloatingActionButton floatingActionButton;
    ImageView imageView;
    String imagepath;
    Button nextImage;
    CardView plateCard;
    TextView plate_txt;
    ProgressBar progressBar;
    CardView regionCard;
    TextView region_txt;
    SharedPreferences sharedPreferences;
    CardView vihicalCard;
    TextView vihical_txt;
    String SHARED_PREF_NAME = "user_pref";
    String token = "";
    String countrycode = "";
    String plate_type = "";
    String region_type = "";
    String car_type = "";
    String last_digits = "";
    String timeStamp = "";
    PickSetup setup = new PickSetup().setTitle("Choose").setCancelText("Cancel").setFlip(true).setMaxSize(50).setWidth(50).setHeight(50).setProgressText("Loading Image").setPickTypes(EPickType.GALLERY, EPickType.CAMERA).setCameraButtonText("Camera").setGalleryButtonText("Gallery").setIconGravity(48).setButtonOrientation(0).setSystemDialog(false).setGalleryIcon(R.drawable.photo).setCameraIcon(R.drawable.cam);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_main);
        this.sharedPreferences = getSharedPreferences(this.SHARED_PREF_NAME, 0);
        this.date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/");
        this.df = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        Button button = (Button) findViewById(R.id.next_image);
        this.nextImage = button;
        button.setOnClickListener(this);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        this.floatingActionButton = floatingActionButton;
        floatingActionButton.setOnClickListener(this);
        this.progressBar = (ProgressBar) findViewById(R.id.homeprogress);
        this.plate_txt = (TextView) findViewById(R.id.car_plate);
        this.region_txt = (TextView) findViewById(R.id.region_code);
        this.vihical_txt = (TextView) findViewById(R.id.vihicle_type);
        this.emptyImage = (ImageView) findViewById(R.id.empty_image);
        this.plateCard = (CardView) findViewById(R.id.cardView);
        this.vihicalCard = (CardView) findViewById(R.id.cardView3);
        this.regionCard = (CardView) findViewById(R.id.cardView2);
        ImageButton imageButton = (ImageButton) findViewById(R.id.setting_edit_btn);
        this.editResult = imageButton;
        imageButton.setOnClickListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        this.imageView = imageView;
        imageView.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.sharedPreferences.contains("checked") && this.sharedPreferences.getBoolean("checked", false)) {
            this.editResult.setVisibility(0);
        } else {
            this.editResult.setVisibility(8);
        }
        this.last_digits = this.sharedPreferences.getString("LastDigits", "");
        String string = this.sharedPreferences.getString("CarToken", "");
        this.token = string;
        if (string.equals("")) {
            Toast.makeText(this.context, "Token Not Found", 0).show();
            return;
        }
        WebRequest.client.addHeader("Authorization", "Token " + this.token);
    }

    @Override // com.vansuita.pickimage.listeners.IPickResult
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams requestParams = new RequestParams();
            String path = r.getPath();
            String compressImage = compressImage(path);
            this.countrycode = this.sharedPreferences.getString("RegionCode", "");
            String string = this.sharedPreferences.getString("BaseUrl", "https://api.platerecognizer.com/v1/plate-reader/");
            Log.d("response", "filepath: " + path + " ");
            try {
                requestParams.put("upload", new File(compressImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            requestParams.put("regions", this.countrycode);
            Log.d("response", "image to upload: " + requestParams + " ");
            WebRequest.post(this.context, string, requestParams, new JsonHttpResponseHandler() { // from class: com.glowingsoft.carplaterecognizer.ui.MainActivity.1
                @Override // com.loopj.android.http.AsyncHttpResponseHandler
                public void onStart() {
                    MainActivity.this.progressBar.setVisibility(0);
                    MainActivity.this.region_txt.setText((CharSequence) null);
                    MainActivity.this.plate_txt.setText((CharSequence) null);
                    MainActivity.this.vihical_txt.setText((CharSequence) null);
                    MainActivity.this.imageView.setImageResource(R.drawable.upload);
                    Log.d("response", "onStart: ");
                    super.onStart();
                }

                @Override // com.loopj.android.http.JsonHttpResponseHandler
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("response ", response.toString() + " ");
                    try {
                        MainActivity.this.imagepath = "https://us-east-1.linodeobjects.com/platerec-api/uploads/" + MainActivity.this.df.format(MainActivity.this.date) + response.getString("filename");
                        JSONArray jSONArray = response.getJSONArray("results");
                        if (jSONArray.length() > 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                String string2 = jSONArray.getJSONObject(i).getString("plate");
                                boolean z = true;
                                String[][] strArr = {new String[]{"kl51l2890", "Megha K S"}, new String[]{"kl49h6635", "Janeesha K A"}, new String[]{"wb52at0350", "Helna Jayan"}};
                                String str = "";
                                int i2 = 0;
                                while (true) {
                                    if (i2 >= 3) {
                                        z = false;
                                        break;
                                    }
                                    String[] strArr2 = strArr[i2];
                                    if (string2.equals(strArr2[0])) {
                                        str = strArr2[1];
                                        break;
                                    }
                                    i2++;
                                }
                                MainActivity.this.plate_txt.setText(string2);
                                if (z) {
                                    MainActivity.this.region_txt.setText("YES");
                                    MainActivity.this.vihical_txt.setText(str);
                                } else {
                                    MainActivity.this.region_txt.setText("NO");
                                    MainActivity.this.vihical_txt.setText("UNKNOWN PASSENGER");
                                }
                                MainActivity.this.timeStamp = response.getString("timestamp");
                                Picasso.with(MainActivity.this.context).load(MainActivity.this.imagepath).into(MainActivity.this.imageView, new Callback() { // from class: com.glowingsoft.carplaterecognizer.ui.MainActivity.1.1
                                    @Override // com.squareup.picasso.Callback
                                    public void onError() {
                                    }

                                    @Override // com.squareup.picasso.Callback
                                    public void onSuccess() {
                                        MainActivity.this.progressBar.setVisibility(8);
                                    }
                                });
                                MainActivity.this.regionCard.setVisibility(0);
                                MainActivity.this.plateCard.setVisibility(0);
                                MainActivity.this.vihicalCard.setVisibility(0);
                                MainActivity.this.nextImage.setVisibility(0);
                                MainActivity.this.floatingActionButton.setVisibility(0);
                                MainActivity.this.emptyImage.setVisibility(8);
                            }
                        }
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }

                @Override // com.loopj.android.http.JsonHttpResponseHandler
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response1", "onFailure: " + errorResponse + " ");
                    MainActivity.this.progressBar.setVisibility(8);
                    MainActivity.this.editResult.setVisibility(8);
                    MainActivity.this.regionCard.setVisibility(8);
                    MainActivity.this.plateCard.setVisibility(8);
                    MainActivity.this.vihicalCard.setVisibility(8);
                    MainActivity.this.nextImage.setVisibility(8);
                    MainActivity.this.floatingActionButton.setVisibility(8);
                    MainActivity.this.emptyImage.setVisibility(0);
                    Toast.makeText(MainActivity.this, errorResponse + "", 0).show();
                }

                @Override // com.loopj.android.http.JsonHttpResponseHandler
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response2", "onFailure: " + errorResponse + " ");
                    MainActivity.this.progressBar.setVisibility(8);
                    MainActivity.this.editResult.setVisibility(8);
                    MainActivity.this.regionCard.setVisibility(8);
                    MainActivity.this.plateCard.setVisibility(8);
                    MainActivity.this.vihicalCard.setVisibility(8);
                    MainActivity.this.nextImage.setVisibility(8);
                    MainActivity.this.emptyImage.setVisibility(0);
                    Toast.makeText(MainActivity.this, errorResponse.toString() + "", 1).show();
                }

                @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("response3", "onFailure: " + responseString + " ");
                    MainActivity.this.progressBar.setVisibility(8);
                    MainActivity.this.editResult.setVisibility(8);
                    MainActivity.this.regionCard.setVisibility(8);
                    MainActivity.this.plateCard.setVisibility(8);
                    MainActivity.this.vihicalCard.setVisibility(8);
                    MainActivity.this.nextImage.setVisibility(8);
                    MainActivity.this.emptyImage.setVisibility(0);
                    Toast.makeText(MainActivity.this, responseString + "No Internet Connection", 1).show();
                }
            });
            return;
        }
        Toast.makeText(this, r.getError().getMessage(), 1).show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == -1) {
            String stringExtra = data.getStringExtra("car_plate");
            String stringExtra2 = data.getStringExtra("region_code");
            String stringExtra3 = data.getStringExtra("car_type");
            Log.d("response", "onActivityResult: " + stringExtra + " ");
            this.plate_txt.setText(stringExtra);
            this.region_txt.setText(stringExtra2);
            this.vihical_txt.setText(stringExtra3);
            Toast.makeText(this, "Results saved", 0).show();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.imageView) {
            if (this.token.isEmpty()) {
                Toast.makeText(this, "Go to Settings to Set Your Token", 1).show();
                return;
            }
            PickImageDialog.build(this.setup).show(this);
        }
        if (v.getId() == R.id.setting_edit_btn) {
            this.plate_type = this.plate_txt.getText().toString();
            this.region_type = this.region_txt.getText().toString();
            this.car_type = this.vihical_txt.getText().toString();
            if (this.plate_type.isEmpty()) {
                Toast.makeText(this, "Nothing to Edit Now", 0).show();
            } else {
                Intent intent = new Intent(this, (Class<?>) EditActivity.class);
                intent.putExtra("car_plate", this.plate_type);
                intent.putExtra("region_code", this.region_type);
                intent.putExtra("car_type", this.car_type);
                startActivityForResult(intent, 123);
            }
        }
        if (v.getId() == R.id.next_image) {
            PickImageDialog.build(this.setup).show(this);
        }
        if (v.getId() == R.id.fab) {
            String charSequence = this.plate_txt.getText().toString();
            String charSequence2 = this.region_txt.getText().toString();
            String charSequence3 = this.vihical_txt.getText().toString();
            Uri localBitmapUri = getLocalBitmapUri(this.imageView);
            String str = "Date & TimeStamp: " + this.timeStamp + "\nCar Plate: " + charSequence + "\nRegion Code: " + charSequence2 + "\nVihicle Type: " + charSequence3 + "\nToken Code: " + this.last_digits;
            Log.d("response", "onActivityResult: " + charSequence + " ");
            if (localBitmapUri != null) {
                Uri.parse("android.resource://" + getPackageName() + "/drawable/ic_launcher");
                Intent intent2 = new Intent();
                intent2.setAction("android.intent.action.SEND");
                intent2.putExtra("android.intent.extra.TEXT", str);
                intent2.putExtra("android.intent.extra.STREAM", localBitmapUri);
                intent2.setType("image/jpeg");
                intent2.addFlags(1);
                startActivity(Intent.createChooser(intent2, "send"));
                return;
            }
            Log.d("response", "onFailure:");
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, (Class<?>) SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), ".Foldername/PlateRecognizer" + System.currentTimeMillis() + ".jpeg");
            file.getParentFile().mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String compressImage(String filePath) {

        int resized=sharedPreferences.getInt("Resize", -1);

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight =resized*7.0f;
        float maxWidth = resized*12.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;

            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth,
                    actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        //      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename(this);
        try {
            out = new FileOutputStream(filename);
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, resized, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static String getFilename(Context context) {
        File file = new File(context.getFilesDir().getPath(), ".Foldername/PlateRecognizerHistory");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int round;
        int i = options.outHeight;
        int i2 = options.outWidth;
        if (i > reqHeight || i2 > reqWidth) {
            round = Math.round(i / reqHeight);
            int round2 = Math.round(i2 / reqWidth);
            if (round >= round2) {
                round = round2;
            }
        } else {
            round = 1;
        }
        while ((i2 * i) / (round * round) > reqWidth * reqHeight * 2) {
            round++;
        }
        return round;
    }
}
