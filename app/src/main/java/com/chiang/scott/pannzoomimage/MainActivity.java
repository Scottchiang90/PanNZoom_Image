package com.chiang.scott.pannzoomimage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG_1 = 1;
    private static int RESULT_LOAD_IMG_2 = 2;

    Bitmap imgBitmap1 = null;
    Bitmap imgBitmap2 = null;

    public static int imgState;
    public static Map<Integer,MyImage> imageMap = null;

    private ImageView imgView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.myImageView);
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageMap = new HashMap<Integer,MyImage>();
    }

    public void clickOne(View v){
        imgState = 1;
        if(imgBitmap1 == null) {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(intent, RESULT_LOAD_IMG_1);
        }
        else{
            imgView.setImageBitmap(imgBitmap1);
        }
    }

    public void clickTwo(View v){
        imgState = 2;
        if(imgBitmap2 == null) {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG_2);
        }
        else{
            imgView.setImageBitmap(imgBitmap2);
        }
    }

    public void saveImg(View v){
        Bitmap bm = loadBitmapFromView(imgView);
        String fileName = DateFormat.getDateTimeInstance().format(new Date()).toString();
        try {
            String path = saveBitmap(bm, fileName);
            Toast.makeText(this, "Saved image to "+path, Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to save", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if(requestCode == RESULT_LOAD_IMG_1 || requestCode == RESULT_LOAD_IMG_2) {
                if (resultCode == RESULT_OK && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    if(requestCode == RESULT_LOAD_IMG_1){
                        // Set the Image in ImageView after decoding the String
                        imgBitmap1 = BitmapFactory.decodeFile(imgDecodableString);
                        imgView.setImageBitmap(imgBitmap1);
                        imageMap.put(1, new MyImage());
                    } else{
                        // Set the Image in ImageView after decoding the String
                        imgBitmap2 = BitmapFactory.decodeFile(imgDecodableString);
                        imgView.setImageBitmap(imgBitmap2);
                        imageMap.put(2, new MyImage());

                    }


                }
            }else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        v.invalidate();
        return b;
    }

    protected String saveBitmap(Bitmap bm, String name) throws Exception {
        String tempFilePath = Environment.getExternalStorageDirectory() + "/"
                + "Pan&Zoom" + "/" + name + ".jpg";
        File tempFile = new File(tempFilePath);
        if (!tempFile.exists()) {
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
        }

        tempFile.delete();
        tempFile.createNewFile();

        int quality = 100;
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);

        bos.flush();
        bos.close();

        bm.recycle();

        return tempFilePath;
    }

}
