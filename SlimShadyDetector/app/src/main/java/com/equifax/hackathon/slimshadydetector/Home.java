package com.equifax.hackathon.slimshadydetector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends AppCompatActivity {

  private RekognitionService rekognitionService;

  private Button takePictureButton;
  private ImageView imageView;
  private Uri file;
  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    rekognitionService = new RekognitionService();
    takePictureButton = findViewById(R.id.button_image);
    imageView = findViewById(R.id.imageview);
    textView = findViewById(R.id.txtResult);

    textView.setText("Will the real slim shady please stand up!?");

    disableTakePictureButtonIfNoPermission();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    enableTakePicutreButtonIfThereIsPermission(requestCode, grantResults);
  }

  public void takePicture(final View view) {
    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    file = Uri.fromFile(getOutputMediaFile());
    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

    startActivityForResult(intent, 100);
  }

  private File getOutputMediaFile() {
    final File mediaStorageDir = new File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      "slimShadyPics"
    );

    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("slimShadyPics", "failed to create directory");
        return null;
      }
    }

    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
  }

  @SuppressLint("StaticFieldLeak")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!(requestCode == 100 && resultCode == RESULT_OK)) {
      return;
    }

    imageView.setImageURI(file);

    textView.setText("Loading...");

    new AsyncTask<ByteBuffer, Integer, Boolean>() {

      @Override
      protected Boolean doInBackground(ByteBuffer... byteBuffers) {
        return rekognitionService.isMatch(byteBuffers[0]);
      }

      protected void onPostExecute(final Boolean isMatch) {
        if(isMatch){
          textView.setText("Match!");
        }else {
          textView.setText("Intruder!");
        }
      }
    }.execute(fileAsBytebuffer());
  }

  private ByteBuffer fileAsBytebuffer() {
    final File fileCopy = new File(this.file.getPath());
    int size = (int) fileCopy.length();
    byte[] bytes = new byte[size];
    try {
      final BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileCopy));
      buf.read(bytes, 0, bytes.length);
      buf.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return ByteBuffer.wrap(bytes);
  }

  private void enableTakePicutreButtonIfThereIsPermission(int requestCode, final int[] grantResults) {
    if (requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
      && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
      takePictureButton.setEnabled(true);
    }
  }

  private void disableTakePictureButtonIfNoPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      takePictureButton.setEnabled(false);
      ActivityCompat.requestPermissions(
        this,
        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
        0
      );
    }
  }
}
