package galos.thegalos.keepdeals;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemView extends AppCompatActivity {

    private static List<Item> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);


        TextView title = findViewById(R.id.tvCardName);
        TextView description = findViewById(R.id.tvCardDescription);
        TextView date = findViewById(R.id.tvDeliveryDate1);
        TextView location = findViewById(R.id.tvDeliveryLocation1);

        ImageView ivPhoto = findViewById(R.id.ivCardPhoto);
        int position = getIntent().getIntExtra("position",0);
        loadData();
        Item item = cards.get(position);
        String str = item.getName();
        title.setText(str);
        str = item.getDescription();
        description.setText(str);
        str = item.getDate();
        date.setText(str);
        str= item.getLocation();
        location.setText(str);

        Bitmap bitmap = getBitmap(item.getCurrentImagePath());
        int height = 0;
        if (bitmap != null) {
            height = bitmap.getHeight() / 3;
            int width = bitmap.getWidth() / 3;
            ivPhoto.setImageBitmap(bitmap);
            ivPhoto.getLayoutParams().height = height;
            ivPhoto.getLayoutParams().width = width;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPhoto.setClipToOutline(true);
            }
        }

    }
    private void loadData() {
        SharedPreferences prefs = getSharedPreferences("galos.thegalos.keepdeals", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("deals", null);
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        cards = gson.fromJson(json, type);

        if (cards == null){
            cards = new ArrayList<>();
        }
    }

    private Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void finish(View v) {
        finish();
    }
}
