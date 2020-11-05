package com.pcts.macrocatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.Duration;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnCamera;
    private EditText edtProposta;
    private EditText edtNome;
    private String tipo;
    private TextView txtFotos;
    private TextView tvPath;
    private RadioGroup radio;

    private String Proposta;

    private Context context;

    public static final int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, fetchPrimaryColor());
        setTaskDescription(taskDesc);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
            toolbar.setPopupTheme(R.style.AppTheme);
        setSupportActionBar(toolbar);

        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setCancelable(false)
                .setDuration(Duration.INDEFINITE)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("jsantos-pcts", "MACRO");
        appUpdater.start();

        btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);
        edtProposta = (EditText) findViewById(R.id.edtProposta);
        edtNome = (EditText) findViewById(R.id.edtNome);
        tvPath = (TextView) findViewById(R.id.tvPath);
        txtFotos = (TextView) findViewById(R.id.txtFotos);
        radio = (RadioGroup) findViewById(R.id.radioIPP_PP);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnCamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });
        btnCamera.setEnabled(false);

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioPP:
                        Proposta = "PP";
                        break;
                    case R.id.radioIPP:
                        Proposta = "IPP";
                        break;
                }

                edtProposta.setText(Proposta);
                int position = edtProposta.length();
                Editable etext = edtProposta.getText();
                Selection.setSelection(etext, position);
            }
        });

        context = this;
    }

    public int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    private void openCameraIntent() {
        //Tirar foto da galeria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        /* Tirar a foto com a APP

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.pcts.macrocatch.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        100);
            }
        }

        */
    }

    /*

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        DialogInterface.OnClickListener dialogClickListener;
        AlertDialog.Builder builder;

        switch(id) {
            case R.id.embarque:
                btnCamera.setEnabled(true);
                edtProposta.setHint("Número de Obra");
                edtProposta.setEnabled(true);
                radio.setEnabled(true);
                edtProposta.setVisibility(View.VISIBLE);
                radio.setVisibility(View.VISIBLE);
                //edtProposta.setText(Proposta);
                tipo = "embarque";
                txtFotos.setText("Embarque");
                break;
            case R.id.obra:
                btnCamera.setEnabled(true);
                edtProposta.setHint("Número de Obra");
                edtProposta.setEnabled(true);
                radio.setEnabled(true);
                edtProposta.setVisibility(View.VISIBLE);
                radio.setVisibility(View.VISIBLE);
                //edtProposta.setText(Proposta);
                tipo = "obra";
                txtFotos.setText("Obra");
                break;
            case R.id.admin:
                btnCamera.setEnabled(true);

                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                btnCamera.setEnabled(true);
                                edtProposta.setHint("Número de Obra");
                                edtProposta.setEnabled(true);
                                radio.setEnabled(true);
                                edtProposta.setVisibility(View.VISIBLE);
                                radio.setVisibility(View.VISIBLE);
                                //edtProposta.setText(Proposta);

                                int position = edtProposta.length();
                                Editable etext = edtProposta.getText();
                                Selection.setSelection(etext, position);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                edtProposta.setEnabled(false);
                                radio.setEnabled(false);
                                edtProposta.setVisibility(View.GONE);
                                radio.setVisibility(View.GONE);
                                edtProposta.setHint("");
                                //edtProposta.setText("");
                                break;
                        }
                    }
                };

                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("A foto é relativa a uma obra?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();

                tipo = "admin";
                txtFotos.setText("Administrativo");
                break;
            case R.id.outros:
                btnCamera.setEnabled(true);

                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                btnCamera.setEnabled(true);
                                edtProposta.setHint("Número de Obra");
                                edtProposta.setEnabled(true);
                                radio.setEnabled(true);
                                edtProposta.setVisibility(View.VISIBLE);
                                radio.setVisibility(View.VISIBLE);
                                //edtProposta.setText(Proposta);

                                int position = edtProposta.length();
                                Editable etext = edtProposta.getText();
                                Selection.setSelection(etext, position);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                edtProposta.setEnabled(false);
                                radio.setEnabled(false);
                                edtProposta.setVisibility(View.GONE);
                                radio.setVisibility(View.GONE);
                                edtProposta.setHint("");
                                //edtProposta.setText("");
                                break;
                        }
                    }
                };

                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("A foto é relativa a uma obra?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();

                tipo = "outros";
                txtFotos.setText("Outros");
                break;
        }

        int position = edtProposta.length();
        Editable etext = edtProposta.getText();
        Selection.setSelection(etext, position);

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            /*
            if (requestCode == 100)
                Glide.with(this).load(imageFilePath).into(imageView);
            else
                return;

            File image = new File(imageFilePath);
            image = new Compressor(this).compressToFile(image);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            imageView.setImageBitmap(bitmap);
            */

            if (requestCode != RESULT_LOAD_IMG) {
                Toast.makeText(this, "Atenção! Ocorreu um erro!", Toast.LENGTH_LONG).show();
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            File image = new File(imgDecodableString);
            Bitmap bitmap = new Compressor(this).compressToBitmap(image);
            imageView.setImageBitmap(bitmap);

            if (bitmap == null) {
                Toast.makeText(this, "Atenção! Ocorreu um erro!", Toast.LENGTH_LONG).show();
                return;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            final String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://pctsintelligentspaces.ddns.net:6969/PCTS_WS/MACRO.asmx/UploadImage";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                response = response.replace("\"", "").trim();

                                if (!response.equals("")) {
                                    tvPath.setText(response.replace("\\\\", "\\"));
                                } else {
                                    Toast.makeText(context, "Atenção, houve um erro, a foto não foi enviada!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro: \n" + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("proposta", edtProposta.getText().toString().trim().toUpperCase());
                    params.put("tipo", tipo);
                    params.put("nome", edtNome.getText().toString().trim());
                    params.put("imagem", encoded);

                    return params;
                }
            };

            queue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(this, "Erro: \n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnCamera.setEnabled(true);
            }
        }*/
    }
}
