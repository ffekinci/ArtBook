package com.example.artbook;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.security.Permission;

public class AddArtActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Bitmap selectedImage;
    Button btn_Delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        imageView = (ImageView) findViewById(R.id.imageView);
        Button button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        btn_Delete = (Button) findViewById(R.id.btn_Delete);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        String name = intent.getStringExtra("name");

        if(info.equals("new")){
            imageView.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.background));
            button.setVisibility(View.VISIBLE);
            btn_Delete.setVisibility(View.INVISIBLE);
            editText.setText("");
        }
        else{
            button.setVisibility(View.INVISIBLE);
            btn_Delete.setVisibility(View.VISIBLE);
            editText.setText(name);

            Database db = Database.getInstance();

            String sql = "SELECT * FROM arts WHERE name = '" + name + "'";

            Cursor cursor = db.getDb().rawQuery("SELECT * FROM arts WHERE name = '" + name + "'", null);

            cursor.moveToFirst();

                byte[] array = cursor.getBlob(cursor.getColumnIndex("image"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                imageView.setImageBitmap(bitmap);

        }


    }

    public void delete(View view){
        String name = editText.getText().toString();

        Database db = Database.getInstance();
        db.execSQL("DELETE FROM arts WHERE name = '"+ name +"'");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void select(View view){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin al
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri image = data.getData();

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                imageView.setImageBitmap(selectedImage);
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view){

        String name = editText.getText().toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte [] arr = outputStream.toByteArray();

        try{
            Database db = Database.getInstance();
            db.setDb(this.openOrCreateDatabase("Arts", MODE_PRIVATE, null));

            db.execSQL("CREATE TABLE IF NOT EXISTS arts(name VARCHAR, image BLOB)");

            String sql = "INSERT INTO arts (name, image) VALUES (?, ?  )";
            SQLiteStatement st = db.getDb().compileStatement(sql);
            st.bindString(1, name);
            st.bindBlob(2, arr);
            db.execStatement(st);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
