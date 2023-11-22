package lk.software.app.firebasestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Document;

import java.util.ArrayList;

import lk.software.app.firebasestorage.adapter.ItemAdapter;

public class ViewItemActivity extends AppCompatActivity {

RecyclerView recyclerView;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;

    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ItemAdapter itemAdapter = new ItemAdapter(items, firebaseStorage, ViewItemActivity.this);
        //LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

//        firestore.collection("items").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        items.clear();
//                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                            Item item = snapshot.toObject(Item.class);
//                            items.add(item);
//                        }
//
//                        itemAdapter.notifyDataSetChanged();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
        firestore.collection("items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        items.clear();
                        //this will update all items regardless of one change;
                        for (DocumentSnapshot snapshot: value.getDocuments()){
                            Item item = snapshot.toObject(Item.class);
                            items.add(item);
                        }
                        //this below can be used if we only need the updated item to be updated in the ui
//                        for(DocumentChange change : value.getDocumentChanges()){
//                            Item item = change.getDocument().toObject(Item.class);
//                            switch(change.getType()){
//                                case ADDED:
//                                    items.add(item);
//                                case MODIFIED:
//                                   Item old =  items.stream().filter(i->
//                                        i.getName().equals(item.getName())
//                                    ).findFirst().orElse(null);
//
//                                   if(old!=null){
//                                       old.setDesc(item.getDesc());
//                                       old.setPrice(item.getPrice());
//                                       old.setImage(item.getImage());
//                                   }
//                                case REMOVED:
//                                    items.remove(item);
//                            }
//                        }
                        itemAdapter.notifyDataSetChanged();
                    }
                });





    }


}