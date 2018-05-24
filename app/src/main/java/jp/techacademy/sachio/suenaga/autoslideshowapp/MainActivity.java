package jp.techacademy.sachio.suenaga.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Cursor mCursor;
    Timer mTimer;
    Handler mHandler = new Handler();
    Button mNextButton;
    Button mBackButton;
    Button mPlaystopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);
        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(this);
        mPlaystopButton = findViewById(R.id.playstop_button);
        mPlaystopButton.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (mCursor.moveToFirst()) {
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.iv);
            imageView.setImageURI(imageUri);

        }
    }

    @Override
    public void onClick(View v) {
        if(mCursor == null){
                Toast ts = Toast.makeText(this,"許可をしてください", Toast.LENGTH_LONG);
                ts.show();
        } else if
                (v.getId() == R.id.next_button ){
            if (mCursor.moveToNext()){

            }else{
                mCursor.moveToFirst();
            }

            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.iv);
            imageView.setImageURI(imageUri);

        }else if (v.getId() == R.id.back_button){
            if (mCursor.moveToPrevious()){

            }else{
                mCursor.moveToLast();
            }

            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.iv);
            imageView.setImageURI(imageUri);

        }else if (v.getId() == R.id.playstop_button){
            if (mTimer == null) {
                mPlaystopButton.setText("停止");
                mNextButton.setEnabled(false);
                mBackButton.setEnabled(false);
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mCursor.moveToNext()){

                                }else{
                                    mCursor.moveToFirst();
                                }

                                int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                                Long id = mCursor.getLong(fieldIndex);
                                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                ImageView imageView = (ImageView) findViewById(R.id.iv);
                                imageView.setImageURI(imageUri);
                            }
                        });
                    }
                }, 2000, 2000);
            }else{
                mPlaystopButton.setText("再生");
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(true);
                mTimer.cancel();
                mTimer = null;
            }

        }



    }

}