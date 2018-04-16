import java.util.ArrayList;
import java.util.List;

public class Kitchen {
    private OrderManager orderManager;
    InventoryManager inventoryManager;
    private List<Cook> cooks;

    //Constructor will take the orderManager from restaurant

    /**
     * The constructor will take an orderManager from the restaurant and initialize new ArrayList of Cook and
     * inventoryManager class
     *
     * @param orderManager The general orderManager used by the restaurant
     */
    Kitchen(OrderManager orderManager) {
        this.orderManager = orderManager;
        this.inventoryManager = new InventoryManager();
        this.cooks = new ArrayList<>();
    }

    Kitchen(OrderManager orderManager, List<Cook> cooks) {
        this.orderManager = orderManager;
        this.inventoryManager = new InventoryManager();
        this.cooks = cooks;
    }

    //Will take an order and the given cook will "cook" it, thereby moving it to next stage

    /**
     * Prompts the given Cook to prepare the given Order
     *
     * @param order
     * @param cook
     */
    public void cook(Order order, Cook cook) {
        orderManager.orderIsCooked(order);
        inventoryManager.useIngredients(order.getAllIngredients());
    }

    public void acceptOrder(Order order, Cook cook) {
        orderManager.acceptOrder(order);
    }

    public Cook getCook(String cookID){
        for (Cook cook : cooks){
            if (cookID.equals(cook.getID())){
                return cook;
            }
        }
        throw new IllegalArgumentException("Cook not found");
    }
}


