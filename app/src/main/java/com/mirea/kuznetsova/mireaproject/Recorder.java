package com.mirea.kuznetsova.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

public class Recorder extends AppCompatActivity {
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonStart = (Button) findViewById(R.id.record);
        buttonStop = (Button) findViewById(R.id.stop_record);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.play_record);
        buttonStopPlayingRecording = (Button)findViewById(R.id.stop_play_rec);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();

        buttonStart.setOnClickListener(view -> {

            if(checkPermission()) {

                AudioSavePathInDevice =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                MediaRecorderReady();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }

                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                Toast.makeText(this, "Recording started",
                        Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }

        });

        buttonStop.setOnClickListener(view -> {
            mediaRecorder.stop();
            buttonStop.setEnabled(false);
            buttonPlayLastRecordAudio.setEnabled(true);
            buttonStart.setEnabled(true);
            buttonStopPlayingRecording.setEnabled(false);

            Toast.makeText(this, "Recording Completed",
                    Toast.LENGTH_LONG).show();
        });

        buttonPlayLastRecordAudio.setOnClickListener(view -> {

            buttonStop.setEnabled(false);
            buttonStart.setEnabled(false);
            buttonStopPlayingRecording.setEnabled(true);

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(AudioSavePathInDevice);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            Toast.makeText(this, "Recording Playing",
                    Toast.LENGTH_LONG).show();
        });

        buttonStopPlayingRecording.setOnClickListener(view -> {
            buttonStop.setEnabled(false);
            buttonStart.setEnabled(true);
            buttonStopPlayingRecording.setEnabled(false);
            buttonPlayLastRecordAudio.setEnabled(true);

            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
        });
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}