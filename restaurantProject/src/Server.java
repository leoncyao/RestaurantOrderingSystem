/**
 * The Server class. Represents a server who builds up orders and submits
 * them to the OrderManager. Servers also bring food back to customers
 */

public class Server {
    private Order currentOrder;
    private String ID;

    public Server(String ID){
        this.ID = ID;
    }

    public void submitOrder(OrderManager manager){
        manager.placeOrder(currentOrder);
    }

    public void retrieveOrder(OrderManager manager, Order order){
        manager.retrieveOrder(order);
    }

    public void confirmOrder(OrderManager manager, Order order){
        manager.confirmCompleted(order);
    }

    public void addFoodtoOrder(Food food){
        currentOrder.addFood(food);
    }

    public void createNewOrder(int tableNumber){
        currentOrder = new Order(tableNumber);
    }

    public String getID() {
        return ID;
    }
}
