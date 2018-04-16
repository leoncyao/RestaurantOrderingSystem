/*
Represents a food.
 */
import java.util.HashMap;
import java.util.Map;

public class Food{
    //Make list of types of ingredients
    private Map<String, Integer> ingredients;
    private float price;
    private String name;

    /**
     * Creates a item on the menu with a name, price, and ingredients
     * @param name The name of the dish
     * @param price The base price of the dish
     * @param ingredients Ingredients used to prepare this dish
     */
    Food(String name, float price, Map<String, Integer> ingredients){
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    // Adds quantity of ingredient to the ingredients map. Creates a new entry if not already present
    public void addIngredient(String ingredientName, int ingredientQuantity){
        if (ingredients.containsKey(ingredientName)){
            int originalQuantity = ingredients.get(ingredientName);
            ingredients.put(ingredientName, originalQuantity + ingredientQuantity);
        }else{
            ingredients.put(ingredientName, ingredientQuantity);
        }
    }
    // Remove quantity of ingredient to the ingredients map. Removes entry if quantity becomes 0
    public void removeIngredient(String ingredientName, int ingredientQuantity){
        if (ingredients.containsKey(ingredientName)) {
            int originalQuantity = ingredients.get(ingredientName);
            if (ingredientQuantity >= originalQuantity) {
                ingredients.remove(ingredientName);
            } else {
                ingredients.put(ingredientName, originalQuantity - ingredientQuantity);
            }
        }
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() { return price; }

    /**
     * This constructor is used STRICTLY for making a copy of the given food
     * @param another The food instance that must be copied
     */
    Food(Food another){
        Map<String, Integer> ingredientsCopy = new HashMap<>(another.ingredients);
        this.name = another.name;
        this.price = another.price;
        this.ingredients = ingredientsCopy;
    }

    public Map<String, Integer> getIngredients(){
        return ingredients;
    }

    @Override
    public String toString() {
        return name;
    }
}
