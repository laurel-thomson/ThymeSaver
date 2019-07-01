package thomson.laurel.beth.thymesaver.Models;

public class Step {
    private String name;

    private boolean isChecked;

    public Step() {
        //required empty constructor for Firebase
    }

    public Step(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
