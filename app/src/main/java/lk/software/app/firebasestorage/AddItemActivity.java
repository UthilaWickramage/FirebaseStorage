package lk.software.app.firebasestorage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreKtxRegistrar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddItemActivity extends AppCompatActivity {

    public static final String TAG = AddItemActivity.class.getName();

    private ImageButton imageButton;
    private FirebaseFirestore firestore;

    private Uri imagePath;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_activity);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we need permission to access gallery implicit intent
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editTextText);
                EditText editText1 = findViewById(R.id.editTextText2);
                EditText editText2 = findViewById(R.id.editTextText3);

                String name = editText.getText().toString();
                String desc = editText1.getText().toString();
                double price = Double.valueOf(editText2.getText().toString());

                String imageId = UUID.randomUUID().toString();
                Item item = new Item(name, desc, price, imageId);

                //ProgressBar bar = new ProgressBar(AddItemActivity.this);


                ProgressDialog dialog = new ProgressDialog(AddItemActivity.this);
                dialog.setMessage("adding the new item");
                dialog.setCancelable(false);
                dialog.show();
                firestore.collection("items").add(item)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if (imagePath != null) {
                                    dialog.setMessage("Uploading Image...");
                                    StorageReference reference = storage.getReference("itemImages")
                                            .child(imageId);

                                    reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            dialog.dismiss();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                            dialog.setMessage("Uploading Image... " + (int) progress + "% done");
                                        }
                                    });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e(TAG, e.getMessage());
                            }
                        });


            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath = result.getData().getData();
                        Log.i(TAG, "Image Path : " + imagePath.getPath());

                        Picasso.get().load(imagePath).fit().centerCrop().into(imageButton);
                        //Picasso.get().load(imagePath).centerCrop().resize(200,200).into(imageButton);

//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                            try {
//                                ImageDecoder.Source source = null;
//                                Bitmap bitmap;
//                                source = ImageDecoder.createSource(getContentResolver(), imagePath);
//                                bitmap = ImageDecoder.decodeBitmap(source);
//                                imageButton.setImageBitmap(bitmap);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            try {
//                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
//
//                                imageButton.setImageBitmap(bitmap);
//                            } catch (Exception e) {
//                                Log.e(TAG, e.getMessage());
//                            }
//                        }


                    }
                }
            }
    );
}