package com.dolan.user.memorize;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = GalleryActivity.class.getSimpleName();
    private MyDbManager manager;
    private EditText textView;

    /**
     * Get Extras From Intent
     * ==================
     */
    private String url;
    private Integer liked;

    /**
     * Button
     * ==================
     */
    Button ubah;
    Button sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        manager = new MyDbManager(this, "", null, 0);
        sim = findViewById(R.id.simpan);
        sim.setEnabled(false);
        ubah = findViewById(R.id.ubah);
        ubah.setOnClickListener(this);
        sim.setOnClickListener(this);
        GetExtras();
    }

    private void GetExtras() {
        ImageView imageView = findViewById(R.id.gambarku);
        textView = findViewById(R.id.teksku);
        if (getIntent().hasExtra("image_url") && getIntent().hasExtra("image_name")) {
            Glide.with(GalleryActivity.this).load(getIntent().getStringExtra("image_url")).into(imageView);
            textView.setText(getIntent().getStringExtra("image_name"));
            url = getIntent().getStringExtra("image_url");
            liked = getIntent().getIntExtra("image_liked", 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GalleryActivity.this, ShowPhotoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ubah:
                textView.setEnabled(true);
                ubah.setEnabled(false);
                sim.setEnabled(true);
                break;
            case R.id.simpan:
                Content content = new Content();
                content.setDeskripsi(textView.getText().toString());
                content.setKode(url);
                content.setLiked(liked);
                boolean result = manager.UpdateData(content);
                if (result) {
                    Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                }
                textView.setEnabled(false);
                sim.setEnabled(false);
                break;
        }
    }
}