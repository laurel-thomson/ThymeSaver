package thomson.laurel.beth.thymesaver.UI.Callbacks;

public interface ValueCallback<T> {
    void onSuccess(T value);
    void onError(String error);
}
