package com.dolan.user.memorize;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageContainer;
    private Integer stateCamera = 1;
    private Integer uploadImage = 0;
    private EditText deskripsi;
    TextView editTanggal;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private ProgressBar progressBar;

    private int tanggal, bulan, tahun;
    private Button bokong;
    private Button mybut;
    private Button add;
    private MyDbManager manager;
    LottieAnimationView anim;
    private LinearLayout relativeLayout;

    final Item[] items = {
            new Item("CAMERA", R.drawable.ic_camera_enhance_black_24dp),
            new Item("FILE", R.drawable.ic_image_black_24dp),
            new Item("BACK", R.drawable.ic_arrow_back_black_24dp)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitializeId();
        SetDateNow();
    }

    private void InitializeId() {
        imageContainer = findViewById(R.id.view_image);
        deskripsi = findViewById(R.id.text_image);
        mybut = findViewById(R.id.my_but);
        editTanggal = findViewById(R.id.edit_tanggal);
        deskripsi.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mybut.setOnClickListener(this);
        Button tanggal = findViewById(R.id.tanggal);
        tanggal.setOnClickListener(this);
        add = findViewById(R.id.add);
        add.setOnClickListener(this);
        bokong = findViewById(R.id.bokong);
        bokong.setOnClickListener(this);
        progressBar = findViewById(R.id.progresbar);
        progressBar.setVisibility(View.INVISIBLE);
        anim = findViewById(R.id.animasku);
        manager = new MyDbManager(MainActivity.this, null, null, 1);
        relativeLayout = findViewById(R.id.bgbg);
    }

    private void SetDateNow() {
        Calendar calendar = Calendar.getInstance();
        tanggal = calendar.get(Calendar.DATE);
        bulan = calendar.get(Calendar.MONTH);
        String myBulan = GenerateDate(bulan);
        tahun = calendar.get(Calendar.YEAR);
        String tungal = tanggal + " " + myBulan + " " + tahun;
        editTanggal.setText(tungal);
    }

    private String GenerateDate(int date) {
        if (date == 0) {
            return "Januari";
        } else if (date == 1) {
            return "Februari";
        } else if (date == 2) {
            return "Maret";
        } else if (date == 3) {
            return "April";
        } else if (date == 4) {
            return "Mei";
        } else if (date == 5) {
            return "Juni";
        } else if (date == 6) {
            return "Juli";
        } else if (date == 7) {
            return "Agustus";
        } else if (date == 8) {
            return "September";
        } else if (date == 9) {
            return "Oktober";
        } else if (date == 10) {
            return "November";
        } else if (date == 11) {
            return "Desember";
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_but:
                SelectImage();
                break;
            case R.id.tanggal:
                dialogPicker();
                break;
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, ShowPhotoActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bokong:
                try {
                    if (imageContainer.getDrawable() != null) {
                        UploadImage();
                    } else {
                        Alerter.create(MainActivity.this)
                                .setBackgroundColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_error_black_24dp)
                                .setText("Silahkan Pilih Gambar Terlebih Dahulu")
                                .enableSwipeToDismiss()
                                .show();
                    }
                } catch (Throwable e) {
                    Toast.makeText(this, "Error Uploading", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void UploadImage() {
        imageContainer.setDrawingCacheEnabled(true);
        imageContainer.buildDrawingCache();
        Bitmap bitmap = imageContainer.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageContainer.setDrawingCacheEnabled(false);
        byte[] data = stream.toByteArray();
        String path = "ourFoto/" + UUID.randomUUID() + ".png";
        StorageReference fireRemes = firebaseStorage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", deskripsi.getText().toString())
                .build();
        UploadTask task = fireRemes.putBytes(data, metadata);
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        add.setEnabled(false);
        mybut.setEnabled(false);
        bokong.setEnabled(false);
        task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                add.setEnabled(true);
                mybut.setEnabled(true);
                bokong.setEnabled(true);
                Uri url = taskSnapshot.getDownloadUrl();
                if (!url.toString().equalsIgnoreCase("")) {
                    AddData(url.toString());
                } else {
                    AddData("www.google.com");
                }

                imageContainer.setImageDrawable(null);
                deskripsi.setText("");
                anim.setVisibility(View.VISIBLE);
                anim.playAnimation();
                anim.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        Log.e("ANIMASI", "ANIMASI MULAI");
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        int delay = 1000;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                anim.setVisibility(View.GONE);
                            }
                        }, delay);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        //noting
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        //noting
                    }
                });
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error nya disini", "None");
            }
        });
    }


    private void SelectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                setTitle("Find Picture").
                setAdapter(getItemList(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
                        }
                        if (items[i].toString().equals("CAMERA")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, stateCamera);
                        } else if (items[i].toString().equals("FILE")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "SELECT FILE"), uploadImage);
                        } else {
                            Log.e("Judul", "Nama Itemsnya : " + items[i].toString());
                            dialogInterface.dismiss();
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    private ListAdapter getItemList() {
        ListAdapter adapter = new ArrayAdapter<Item>(this, android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);
                return view;
            }
        };
        return adapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == stateCamera) {
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imageContainer.setImageBitmap(bmp);
            } else if (requestCode == uploadImage) {
                Uri selectImageUrl = data.getData();
                imageContainer.setImageURI(selectImageUrl);
            }
        }
    }

    private DatePickerDialog.OnDateSetListener picker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            tanggal = i2;
            bulan = i1;
            tahun = i;
            String tungal = tanggal + " " + GenerateDate(bulan) + " " + tahun;
            editTanggal.setText(tungal);
        }
    };

    private void dialogPicker() {
        SetDateNow();
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, picker, tahun, bulan, tanggal);
        dialog.show();
    }

    private void AddData(String kode) {
        Content e = new Content();
        e.setKode(kode);
        e.setDeskripsi(deskripsi.getText().toString());
        e.setTanggal(editTanggal.getText().toString());
        e.setBulan(String.valueOf(bulan));
        e.setLiked(0);
        boolean result = manager.AddData(e);
        if (result) {
            Toast.makeText(this, "Penambahan Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Penambahan Gagal", Toast.LENGTH_SHORT).show();
        }
    }

    public static class Item {
        public final String text;
        public final int icon;

        public Item(String text, int icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}