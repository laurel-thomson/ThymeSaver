package thomson.laurel.beth.thymesaver.Database;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;

public interface IStorageRepository {
    void uploadImage(Bitmap image, String recipeName);

    StorageReference getImageReference(String recipeName);

    void deleteImage(String recipeName);
}
