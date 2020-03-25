package galos.thegalos.keepdeals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemAdd extends AppCompatActivity {

    private EditText etName, etDescription, etLocation;
    private static List<Item> deliveries = new ArrayList<>();
    private Button btnAddDelivery, btnDate, btnCamera;
    private String currentImagePath = null;
    private final int CAMERA_REQUEST = 1, WRITE_PERMISSION_REQUEST = 2;
    private ImageView ivPhoto;
    private boolean photoTaken = false, dateSelected = false, saveData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        btnDate = findViewById(R.id.btnDate);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        btnAddDelivery = findViewById(R.id.btnAddDelivery);
        btnCamera = findViewById(R.id.btnCamera);
        ivPhoto = findViewById(R.id.ivPhoto);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .7));
        loadData();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                checkCells();
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                checkCells();
            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                checkCells();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                    } else
                        captureImage();
                } else
                    captureImage();
            }
        });

        btnAddDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photoTaken) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemAdd.this); //alert for confirm to delete
                    builder.setTitle(R.string.pay_attention);
                    builder.setMessage(R.string.saved_card_without_photo);    //set message
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            addItem();

                        }
                    }).setNegativeButton(R.string.take_photo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnCamera.performClick();
                        }
                    }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();  //show alert dialog
                } else
                    addItem();
            }
        });
    }

    private void addItem() {
        String name = etName.getText().toString();
        String desc = etDescription.getText().toString();
        String date = btnDate.getText().toString();
        String location = etLocation.getText().toString();

        Item workout = new Item(name, desc, date, location, currentImagePath);
        deliveries.add(workout);
        saveData();
        Intent intent = new Intent(ItemAdd.this, MainMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        saveData = true;
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
            } else
                captureImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK) {
            Bitmap bitmap=getBitmap(currentImagePath);
            ivPhoto.setImageBitmap(bitmap);
            ivPhoto.setVisibility(View.VISIBLE);
            photoTaken = true;
            checkCells();
        }
    }

    private Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Matrix matrix = new Matrix();
            int angle = 90;
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File getImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(timeStamp,".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void captureImage() {
        if(photoTaken) {
            ivPhoto.setImageBitmap(null);
            ivPhoto.setVisibility(View.GONE);
            photoTaken = false;
        }
            deleteFile();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try{
                imageFile = getImageFile();
            }catch(IOException e){
                e.printStackTrace();
            }

            if (imageFile != null){
                Uri imageUri = FileProvider.getUriForFile(this,"galos.thegalos.keepdeals.fileprovider",imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
            }
        }
    }

    // save data
    private void saveData() {
        SharedPreferences prefs = getSharedPreferences("galos.thegalos.keepdeals", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deliveries);
        editor.putString("deals", json);
        editor.apply();
    }

    // load data
    private void loadData() {
        SharedPreferences prefs = getSharedPreferences("galos.thegalos.keepdeals", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("deals", null);
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        deliveries = gson.fromJson(json, type);

        if (deliveries == null) {
            deliveries = new ArrayList<>();
        }
    }

    // date picker
    public void showDatePicker(View v) {
        final Calendar calDate = Calendar.getInstance();
        int day = calDate.get(Calendar.DAY_OF_MONTH);
        int month = calDate.get(Calendar.MONTH);
        int year = calDate.get(Calendar.YEAR);

        // date picker dialog
        DatePickerDialog datePicker = new DatePickerDialog(ItemAdd.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String strDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        btnDate.setText(strDate);
                        dateSelected = true;
                        checkCells();
                    }
                }, year, month, day);
        datePicker.show();
    }

    // check if all cells filled
    private void checkCells() {
        if (etName.getText().toString().length() == 0 || etDescription.getText().toString().length() == 0
                || !dateSelected || etLocation.getText().toString().length() == 0)
            btnAddDelivery.setEnabled(false);
        else
            btnAddDelivery.setEnabled(true);

    }


    // reason for override is if user clicked outside of dialog activity to remove the taken photo
    @Override
    public void finish() {
        if(!saveData) {
            if(photoTaken)
                deleteFile();
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        // if photo already taken remove it from memory
        finish();
    }

    private void deleteFile(){
        // delete saved photo from device memory
        if (currentImagePath!=null) {
            File file = new File(currentImagePath);
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }
            }
        }
    }
}
