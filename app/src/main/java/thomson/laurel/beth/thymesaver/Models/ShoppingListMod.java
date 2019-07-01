package thomson.laurel.beth.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class ShoppingListMod {
    private String name;
    private int quantity;

    //the type of modification: change (for non-bulk ingredients) or add/delete (for bulk ingredients)
    private ModType type;

    public ShoppingListMod() {}

    public ShoppingListMod(String name, ModType type, int quantity) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public ModType getType() {
        return type;
    }

    public void setType(ModType type) {
        this.type = type;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
