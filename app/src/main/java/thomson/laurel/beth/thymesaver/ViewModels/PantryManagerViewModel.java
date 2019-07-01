package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.IPantryManagerRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.PantryManagerRepository;
import thomson.laurel.beth.thymesaver.Models.Pantry;
import thomson.laurel.beth.thymesaver.Models.Follower;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;

import java.util.List;

public class PantryManagerViewModel extends AndroidViewModel {
    private IPantryManagerRepository mRepository;
    private LiveData<List<Pantry>> mPantries;
    private LiveData<List<Follower>> mPantryRequests;

    public PantryManagerViewModel(@NonNull Application application) {
        super(application);
        mRepository = PantryManagerRepository.getInstance();
        mPantries = mRepository.getPantries();
        mPantryRequests = mRepository.getFollowers();
    }

    public LiveData<List<Pantry>> getPantries() {
        return mPantries;
    }

    public LiveData<List<Follower>> getPantryRequests() {
        return mPantryRequests;
    }

    public void requestJoinPantry(String email, Callback callBack) {
        mRepository.trySendJoinPantryRequest(email, callBack);
    }

    public void acceptJoinRequest(Follower request) {
        mRepository.acceptJoinRequest(request);
    }

    public void declineJoinRequest(Follower request) {
        mRepository.declineJoinRequest(request);
    }

    public void updatePreferredPantry(String pantryId) {
        mRepository.updatePreferredPantry(pantryId);
    }

    public String getPreferredPantryId() {
        return mRepository.getPreferredPantryId();
    }

    public void leavePantry(Pantry pantry) {
        mRepository.leavePantry(pantry);
    }

    public void removeFollower(Follower follower) {
        mRepository.removeFollower(follower);
    }
}
