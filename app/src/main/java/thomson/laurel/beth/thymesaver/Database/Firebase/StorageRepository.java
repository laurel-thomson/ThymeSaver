package thomson.laurel.beth.thymesaver.Database.Firebase;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import thomson.laurel.beth.thymesaver.Database.IStorageRepository;


public class StorageRepository implements IStorageRepository {
    private static StorageRepository mSoleInstance;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    public static StorageRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new StorageRepository();
        }
        return mSoleInstance;
    }

    private StorageRepository() {
        mStorage = FirebaseStorage.getInstance();
        String pantryId = DatabaseReferences.getPreferredPantry();
        mStorageReference = mStorage.getReference().child("images").child(pantryId);
    }

    public void uploadImage(Bitmap bitmap, String recipeName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference fileRef = mStorageReference.child(recipeName + ".jpg");
        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public StorageReference getImageReference(String recipeName) {
        return mStorageReference.child(recipeName + ".jpg");
    }

    public void deleteImage(String recipeName) {
        StorageReference fileRef = mStorageReference.child(recipeName + ".jpg");
        fileRef.delete();
    }
}
