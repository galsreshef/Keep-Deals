package galos.thegalos.keepdeals;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyWorkoutViewHolder> {

    private final List<Item> deliveries;
    private myDeliveryListener listener;

    interface myDeliveryListener {
        void onClickListener(int position);
        void onDeliveryLongClicked();
    }

    public void setListener(myDeliveryListener listener){
        this.listener = listener;
    }

    public ItemAdapter(List<Item> deliveries) {
        this.deliveries = deliveries;
    }

    public class MyWorkoutViewHolder extends RecyclerView.ViewHolder{

        final TextView workoutName;
        final TextView desc;
        final TextView date;
        final TextView location;
        final ImageView deliveryImage;

        MyWorkoutViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutName = itemView.findViewById(R.id.tvCardName);
            desc = itemView.findViewById(R.id.tvCardDescription);
            date =  itemView.findViewById(R.id.tvDate);
            location = itemView.findViewById(R.id.tvLocation);
            deliveryImage =  itemView.findViewById(R.id.ivCardPhoto);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onClickListener(getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null)
                        listener.onDeliveryLongClicked();
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public MyWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        return new MyWorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkoutViewHolder holder, int position) {

        Item delivery = deliveries.get(position);
        holder.workoutName.setText(delivery.getName());
        holder.desc.setText(delivery.getDescription());
        holder.date.setText(delivery.getDate());
        holder.location.setText(delivery.getLocation());
        Bitmap bitmap = getBitmap(delivery.getCurrentImagePath());
        holder.deliveryImage.setImageBitmap(bitmap);
    }

    private Bitmap getBitmap(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(path,options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);


            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

}

