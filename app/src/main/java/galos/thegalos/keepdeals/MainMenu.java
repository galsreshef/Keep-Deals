package galos.thegalos.keepdeals;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    private static List<Item> deliveries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        //  private HashMap<String, Exercise> exercises;
        ImageView ivAddDelivery = findViewById(R.id.ivAddDelivery);

        RecyclerView recyclerView = findViewById(R.id.rvDelivery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivAddDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, ItemAdd.class));
            }
        });
        loadData();

        final ItemAdapter deliveryAdapter = new ItemAdapter((deliveries));
        deliveryAdapter.setListener(new ItemAdapter.myDeliveryListener() {
            @Override
            public void onClickListener(int position) {
                Intent intent = new Intent(MainMenu.this, ItemView.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }

            @Override
            public void onDeliveryLongClicked() {
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder draggedItem, RecyclerView.ViewHolder target) {
                int positionDragged = draggedItem.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(deliveries, positionDragged, positionTarget);
                deliveryAdapter.notifyItemMoved(positionDragged, positionTarget);
                saveData();
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.START || direction == ItemTouchHelper.END) {    //if swipe to either side

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this); //alert for confirm to delete
                    builder.setMessage(R.string.are_you_sure);    //set message
                    builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = deliveries.get(position).getCurrentImagePath();

                            // if somehow item was saved with no photo or photo was deleted this will prevent app crash!
                            if (str != null)
                                deletePhoto(str);

                            deliveryAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            deliveries.remove(position);
                            saveData();

                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //notifies the RecyclerView Adapter to reload swiped item
                            deliveryAdapter.notifyItemRangeChanged(position, deliveryAdapter.getItemCount());
                        }
                    }).show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView); //set swipe to recylcerview
        recyclerView.setAdapter(deliveryAdapter);

    }

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

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences("galos.thegalos.keepdeals", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deliveries);
        editor.putString("deals", json);
        editor.apply();
    }

    private void deletePhoto(String path) {
        // delete saved photo from device memory
        File file = new File(path);
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
