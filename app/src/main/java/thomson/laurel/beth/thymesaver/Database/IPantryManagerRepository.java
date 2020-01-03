package thomson.laurel.beth.thymesaver.Database;

import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Pantry;
import thomson.laurel.beth.thymesaver.Models.Follower;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;

import java.util.List;

public interface IPantryManagerRepository {
    void initializePreferredPantry(Callback callback);

    void updatePreferredPantry(String pantryId);

    void trySendJoinPantryRequest(String email, Callback callBack);

    void acceptJoinRequest(Follower request);

    void declineJoinRequest(Follower request);

    void leavePantry(Pantry pantry);

    void removeFollower(Follower follower);

    String getPreferredPantryId();

    LiveData<List<Pantry>> getPantries();

    LiveData<List<Follower>> getFollowers();
}
