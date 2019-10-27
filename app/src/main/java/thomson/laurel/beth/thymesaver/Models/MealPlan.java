package thomson.laurel.beth.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class MealPlan {
    private String recipeName;
    private String scheduledDay;
    private String imageURL;
    private String firebaseKey;

    public MealPlan() {
        //required empty constructor for Firebase
    }

    public MealPlan(String recipeName, String scheduledDay) {
        this.recipeName = recipeName;
        this.scheduledDay = scheduledDay;
    }

    public MealPlan(String recipeName, String scheduledDay, String imageURL) {
        this.recipeName = recipeName;
        this.scheduledDay = scheduledDay;
        this.imageURL = imageURL;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getScheduledDay() {
        return scheduledDay;
    }

    public void setScheduledDate(String day) {
        this.scheduledDay = day;
    }

    @Exclude
    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
