package thomson.laurel.beth.thymesaver.Database.Firebase.LiveData;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class
ListLiveData<T> extends LiveData<DataSnapshot> {
    private List<T> mQueryValuesList = new ArrayList<>();
    private final Class<T> mClassType;
    private final Query mQuery;
    private final MyEventListener mListener = new MyEventListener();

    public ListLiveData(
            Query q,
            Class<T> classType) {
        mQuery = q;
        mClassType = classType;
    }

    @Override
    protected void onActive() {
        mQuery.addValueEventListener(mListener);
    }

    @Override
    protected void onInactive() {
        mQuery.removeEventListener(mListener);
    }

    private class MyEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null){
                setValue(dataSnapshot);
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    T value = snap.getValue(mClassType);
                    mQueryValuesList.add(value);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
