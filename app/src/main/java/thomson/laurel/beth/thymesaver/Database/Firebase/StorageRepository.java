package thomson.laurel.beth.thymesaver.Database.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class StorageRepository {
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

    public void uploadImage(Uri image) {
        StorageReference fileRef = mStorageReference.child(image.getLastPathSegment());
        UploadTask task = fileRef.putFile(image);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //TODO: on Success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO: on Failure
            }
        });
    }

    public StorageReference getImageReference(String recipeName) {
        return mStorageReference.child(recipeName + ".jpg");
    }
}
