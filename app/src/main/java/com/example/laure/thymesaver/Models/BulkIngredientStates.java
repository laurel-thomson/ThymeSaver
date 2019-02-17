package com.example.laure.thymesaver.Models;

public enum BulkIngredientStates {
    OUT_OF_STOCK,
    RUNNING_LOW,
    IN_STOCK;

    public static BulkIngredientStates convertIntToEnum(int x) {
        switch(x) {
            case 0:
                return OUT_OF_STOCK;
            case 1:
                return RUNNING_LOW;
            default:
                return IN_STOCK;
        }
    }

    public static int convertEnumToInt(BulkIngredientStates state) {
        switch (state) {
            case OUT_OF_STOCK:
                return 0;
            case RUNNING_LOW:
                return 1;
            default:
                return 2;
        }
    }

    public static int getNextStateAsInt(int stateAsInt) {
        switch (stateAsInt) {
            case 0:
                return 2;
            case 1:
                return 0;
            default:
                return 1;
        }
    }
}
