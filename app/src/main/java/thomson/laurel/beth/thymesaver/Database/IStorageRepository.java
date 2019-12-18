package thomson.laurel.beth.thymesaver.Database;

import android.graphics.Bitmap;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

public interface IStorageRepository {
    void uploadImage(Bitmap image, String recipeName, ValueCallback<String> callback);
}
