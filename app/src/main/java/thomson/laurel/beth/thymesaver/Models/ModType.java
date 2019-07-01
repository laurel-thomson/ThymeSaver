package thomson.laurel.beth.thymesaver.Models;

public enum ModType {
    CHANGE, //the mod changes the quantity of a non-bulk ingredient (may have a starting shopping list quantity of 0)
    ADD, //the mod adds an existing bulk ingredient from the pantry to the shopping list
    DELETE, //the mod deletes an existing ingredient from the shopping list
    NEW //the mod adds an ingredient that is not in the pantry to the shopping list (the user may or
        //may not want to put the ingredient in the pantry when the item is checked off
}
