package com.rafaelfilgueiras.fabricadememe;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    Button btnGO;

    TextView edtView1, edtView2;

    EditText edtTop_text, edtBotton_text;

    ImageView imageView;

    String currentImage = "";


    private static final int MY_PERMISSION_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 2;

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_Carregar:
                    // Opcao para carregar a imagem <--
                    //item.setEnabled(false);
                    //mTextMessage.setText(R.string.title_home);
                    Intent intentCarregar = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentCarregar, RESULT_LOAD_IMAGE);
                    return true;

                case R.id.navigation_Salvar:
                    //mTextMessage.setText(R.string.title_dashboard);
                    View content = findViewById(R.id.lay);
                    Bitmap bitmap = getScreenShot(content);
                    currentImage = "meme" + System.currentTimeMillis() + ".png";
                    store(bitmap, currentImage);

                    return true;

                case R.id.navigation_Share:
                    //item.setEnabled(false);
                    //mTextMessage.setText(R.string.title_notifications);
                    shareImage(currentImage);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            // Não executa trattiva
        }

        imageView = findViewById(R.id.imageView);
        edtView1 = findViewById(R.id.edtView1);
        edtView2 = findViewById(R.id.edtView2);
        edtTop_text = findViewById(R.id.edtTop_text);
        edtBotton_text = findViewById(R.id.edtBotton_text);
        btnGO = findViewById(R.id.btnGO);

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtView1.setText(edtTop_text.getText().toString());
                edtView2.setText(edtBotton_text.getText().toString());

                edtTop_text.setText("");
                edtBotton_text.setText("");


            }
        });

    }

    public static Bitmap getScreenShot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MEME";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Salvo!", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Falha ao Salvar!", Toast.LENGTH_LONG).show();
        }
    }

    private void shareImage(String fileName){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MEME";
        Uri uri = Uri.fromFile(new File(dirPath, fileName));
        Intent intentShare = new Intent();
        intentShare.setAction(Intent.ACTION_SEND);
        intentShare.setType("image/*");

        intentShare.putExtra(Intent.EXTRA_SUBJECT, "");
        intentShare.putExtra(Intent.EXTRA_TEXT, "");
        intentShare.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(intentShare, "Compartilhar por: "));

        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "Não foi encontrado app de compartilhamento", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColum = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColum, null,null,null);
            cursor.moveToFirst();
            int columIndex = cursor.getColumnIndex(filePathColum[0]);
            String picturePath = cursor.getString(columIndex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        // nada por hora
                    }
                }else {
                    Toast.makeText(this, "Não foram dadas permissões", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
