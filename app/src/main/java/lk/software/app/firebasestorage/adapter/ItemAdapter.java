package lk.software.app.firebasestorage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.software.app.firebasestorage.Item;
import lk.software.app.firebasestorage.MainActivity;
import lk.software.app.firebasestorage.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHOlder> {

    public static final String TAG = ItemAdapter.class.getName();
    private ArrayList<Item> items;
    private FirebaseStorage storage;
    private Context context;

    public ItemAdapter(ArrayList<Item> items, FirebaseStorage storage, Context context) {
        this.items = items;
        this.storage = storage;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, String.valueOf(viewType));
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHOlder holder, int position) {
        Item item = items.get(position);

        holder.textname.setText(item.getName());
        holder.textdesc.setText(item.getDesc());
        holder.textprice.setText(String.valueOf(item.getPrice()));


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }
        });


        storage.getReference("itemImages/"+item.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(100,100).centerCrop().into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHOlder extends RecyclerView.ViewHolder {

        TextView textname, textdesc, textprice;
        ImageView imageView;

        public ViewHOlder(@NonNull View v) {
            super(v);
            textname = v.findViewById(R.id.name);
            textdesc = v.findViewById(R.id.description);
            textprice = v.findViewById(R.id.price);
            imageView = v.findViewById(R.id.itemImage);
        }
    }
}
