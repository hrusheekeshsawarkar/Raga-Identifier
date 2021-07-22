package com.shahdivya.ragaidentification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.shahdivya.ragaidentification.Retrofit.INodeJS;
import com.shahdivya.ragaidentification.Retrofit.RetroFitClient;
import com.shahdivya.ragaidentification.models.RagaPredicted;
import com.shahdivya.ragaidentification.models.Task;

import java.io.File;
import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1 ;
    private static final int REQUEST_SOUND = 200;
    INodeJS api;
    Button btFile;
    ImageView btPlay,btPause;
    ProgressBar pb;
    TextView desc;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit = RetroFitClient.getInstance();
        api =retrofit.create(INodeJS.class);
        btFile = findViewById(R.id.file);
        pb = findViewById(R.id.progressBar);
        desc = findViewById(R.id.textView2);
        seekBar = findViewById(R.id.seekbar);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);

        btFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btFile.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                desc.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT>23)
                {
                    if (checkPermission())
                        filepicker();
                    else {
                        requestPermissions();
                    }
                }else {
                    filepicker();
                }
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(runnable,500);
            }
        };

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable,0);
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){ mediaPlayer.seekTo(i); }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });*/
    }

    private void filepicker() {
        Intent chooseFile = new Intent();
        chooseFile.setAction(Intent.ACTION_PICK);
        chooseFile.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseFile,REQUEST_SOUND);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED ))
        {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Permissions Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            String FilePathStr = null;
            switch (requestCode){
                case REQUEST_SOUND:
                    if (data!=null)
                    {
                        Uri selectedImage = data.getData();
                        mediaPlayer = MediaPlayer.create(this,selectedImage);
                        btPlay.setVisibility(View.GONE);
                        btPause.setVisibility(View.VISIBLE);
                        seekBar.setVisibility(View.VISIBLE);
                        seekBar.setMax(mediaPlayer.getDuration());
                        handler.postDelayed(runnable,0);
                        mediaPlayer.start();
                        String[] filepath = {MediaStore.Audio.Media.DATA};
                        Cursor c = getContentResolver().query(selectedImage,filepath,null,null,null,null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filepath[0]);
                        FilePathStr = c.getString(columnIndex);
                        c.close();
                        uploadImage(FilePathStr);

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                btPause.setVisibility(View.GONE);
                                btPlay.setVisibility(View.VISIBLE);
                                mediaPlayer.seekTo(0);
                            }
                        });


                    }
            }
        }
    }

    private void uploadImage(String filepath)
    {
        Log.d("FilePath",filepath);
        File file= new File(filepath);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        MultipartBody.Part parts = MultipartBody.Part.createFormData("file",file.getName(),requestBody);
        Call<RagaPredicted> ragaPredictedCall = api.uploaded(parts);
        ragaPredictedCall.enqueue(new Callback<RagaPredicted>() {
            @Override
            public void onResponse(Call<RagaPredicted> call, Response<RagaPredicted> response) {
                pb.setVisibility(View.GONE);
                btFile.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                if(response.isSuccessful()){
                    if (response.body().getError().equalsIgnoreCase("0"))
                    {
                        desc.setText("The Raga Predicted for the given audio file is:"+response.body().getPrediction()+"\nIf you want to try another you can click on the button below.");
                    }else {
                        desc.setText("An error has occurred which states :"+response.body().getError()+"\nIf you want to try another you can click on the button below.");
                    }
                }
            }
            @Override
            public void onFailure(Call<RagaPredicted> call, Throwable t) {
                pb.setVisibility(View.GONE);
                btFile.setVisibility(View.VISIBLE);
                desc.setText("An error has occurred which states :"+t.getLocalizedMessage()+"\nIf you want to try another you can click on the button below.");
            }
        });
    }

    private void requestPermissions()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Toast.makeText(this, "Give Permission", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result ==PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }else {
            return false;
        }
    }
}