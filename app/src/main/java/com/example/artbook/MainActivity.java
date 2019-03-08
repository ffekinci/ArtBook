package com.example.artbook;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Blob;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.add_art){
            Intent intent = new Intent(getApplicationContext(), AddArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> name = new ArrayList<>();
        ArrayList<Bitmap> blob = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, name);
        listView.setAdapter(adapter);

        try{

            Database db = Database.getInstance();
            db.setDb(this.openOrCreateDatabase("Arts", MODE_PRIVATE, null));

            db.execSQL("CREATE TABLE IF NOT EXISTS arts(name VARCHAR, image BLOB)");

            Cursor cursor = db.getDb().rawQuery("SELECT * FROM arts", null);

            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");

            cursor.moveToFirst();

            while (cursor != null){
                name.add(cursor.getString(nameIx));

                byte[] array = cursor.getBlob(imageIx);
                Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                blob.add(bitmap);

                cursor.moveToNext();

                adapter.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), AddArtActivity.class);
                intent.putExtra("info", "old");
                intent.putExtra("name", name.get(position));
                startActivity(intent);
            }
        });

    }
}
