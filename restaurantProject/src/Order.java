import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Order class. Orders contain a list of foods to be cooked and are passed through OrderManager and are
 * given a tableNumber
 */

public class Order {
    public List<Food> foods;
    private int tableNumber;
    private static int classOrderNumber = 1;
    int orderNumber;

    /**
     * Creates an Order with a tableNumber, OrderNumber, and list of foods.
     * OrderNumber is set based on the number of Orders taken already
     * @param tableNumber The number of the table
     */
    public Order(int tableNumber){
        this.tableNumber = tableNumber;
        this.orderNumber = classOrderNumber;
        classOrderNumber += 1;
        foods = new ArrayList<>();
    }

    //Returns a Map with the name and amount of each ingredient
    public Map<String, Integer> getAllIngredients(){
        Map<String, Integer> allIngredients = new HashMap<>();

        for (Food food : foods){
            Map<String, Integer> currentIngredients = food.getIngredients();
            for (Map.Entry<String, Integer> entry : currentIngredients.entrySet()) {
                String name = entry.getKey();
                Integer quantity = entry.getValue();
                if (allIngredients.containsKey(name)){
                    int originalQuantity = allIngredients.get(name);
                    allIngredients.replace(name, originalQuantity, originalQuantity + quantity);
                }else{
                    allIngredients.put(name, quantity);
                }
            }
        }
        return allIngredients;
    }

    //Adds the food to the order
    public void addFood(Food food){
        foods.add(food);
    }

    //Returns the total price of all the foods
    public float getPrice(){
        float total = 0;
        for (Food food : foods){
            total += food.getPrice();
        }
        return total;
    }

    public int getTableNumber() {
        return tableNumber;
    }
}
