import java.io.*;
import java.util.*;

// The main class of the project that controls all other classes
public class Restaurant {
    private OrderManager orderManager;
    private Kitchen kitchen;
    private List<Server> servers;
    private Map<String, Food> menu;

    private static final String MENU_FILE = "phase1/restaurantProject/src/menu.txt";
    private static final String EVENT_FILE = "phase1/restaurantProject/src/events.txt";

    public Restaurant() {
        this.orderManager = new OrderManager();
        this.kitchen = new Kitchen(orderManager);
        this.menu = new HashMap<>();

        constructMenu(MENU_FILE);
    }

    public Restaurant(List<Server> servers, List<Cook> cooks){
        this.servers = servers;
        this.orderManager = new OrderManager();
        this.kitchen = new Kitchen(orderManager, cooks);
        this.menu = new HashMap<>();

        constructMenu(MENU_FILE);
    }

    //Generates the Menu from the menu.txt file
    private void constructMenu(String file) {
        try {
            //Create the menu file if it does not exist
            if (!(new File(file).exists())) {
                new PrintWriter(new BufferedWriter(new FileWriter(file)));}

            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            Set<String> ingredientTypes = new HashSet<>();

            // Iterate through the lines from the file starting at 1.
            String line = fileReader.readLine();
            while (line != null){
                Map<String, Integer> allIngredients = new HashMap<>();

                //First item is name, rest is ingredients
                String[] barSplit = line.split("\\|");
                Float price = Float.valueOf(barSplit[0].trim());
                String foodName = barSplit[1].trim();

                //Each ingredient is separated by a comma
                String[] ingredients = barSplit[2].split(",");
                for (String s : ingredients){
                    //There is an "x" between the amount and the ingredient name
                    String[] ingredient = s.split("x");
                    String ingredientName = ingredient[1].trim();
                    Integer ingredientAmount = Integer.valueOf(ingredient[0].trim());

                    allIngredients.put(ingredientName, ingredientAmount);
                    ingredientTypes.add(ingredientName);
                }

                menu.put(foodName, new Food(foodName, price, allIngredients));
                line = fileReader.readLine();
            }
            kitchen.inventoryManager.checkIntegrity(ingredientTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Iterates through every line in the events.txt file
    private void processEvents(String file){
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));

            String line = fileReader.readLine();
            while (line != null){

                processEvent(line);
                line = fileReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Takes one event line from the file and processes it
    private void processEvent(String event) {
        String notes = "";

        String[] split = event.split("\\|");

        String workerName = split[0].trim();
        String eventType = split[1].trim();
        String orderId = split[2].trim();

        if (split.length == 4){
            notes = split[3].trim();
        }

        switch (eventType){
            case "takeOrder":
                Order myOrder = parseOrder(notes);
                Server orderServer = getServer(workerName);

                orderManager.placeOrder(myOrder);
                System.out.println("Order " + myOrder.orderNumber + " placed with foods: " + myOrder.foods + " by Server " + orderServer.getID());
                break;
            case "cookConfirmOrder":
                Order toConfirm = orderManager.getOrder(Integer.valueOf(orderId), "pending");
                Cook confirmingCook = kitchen.getCook(workerName);

                kitchen.acceptOrder(toConfirm, confirmingCook);
                System.out.println("Cook " + confirmingCook.getID() + " confirmed order" + toConfirm.orderNumber);
                break;
            case "cookFinishedOrder":
                Order toFill = orderManager.getOrder(Integer.valueOf(orderId), "in progress");
                Cook cookingCook = kitchen.getCook(workerName);

                kitchen.cook(toFill, cookingCook);
                System.out.println("Cook " + cookingCook.getID() + " cooked order" + toFill.orderNumber);
                break;
            case "tableReceivedOrder":
                Order toReceive = orderManager.getOrder(Integer.valueOf(orderId), "cooked");
                Server receivingServer = getServer(workerName);

                orderManager.retrieveOrder(toReceive);
                orderManager.confirmCompleted(toReceive);
                System.out.println("Server " + receivingServer.getID() + " gave order " + toReceive.orderNumber + " to table " + toReceive.getTableNumber());
                break;
            case "tableRejectedOrder":
                Order toReject = orderManager.getOrder(Integer.valueOf(orderId), "cooked");
                Server rejectingServer = getServer(workerName);

                orderManager.retrieveOrder(toReject);
                System.out.println("Server " + rejectingServer.getID() + " rejected order " + toReject.orderNumber + " from table " + toReject.getTableNumber() + " for reason " + notes);
                break;
            case "tableRequestedBill":
                Order toPay = orderManager.getOrder(Integer.valueOf(orderId), "completed");
                Server billServer = getServer(workerName);

                System.out.println("Server " + billServer.getID() + " gave bill of " + toPay.getPrice() + " to table " + toPay.getTableNumber());
                break;
            case "receiveShipment":
                Map<String, Integer> inventoryShipment = parseShipment(notes);
                kitchen.inventoryManager.receiveShipment(inventoryShipment);

                System.out.println("Received shipment of " + inventoryShipment);
                break;
            default:
                System.out.println("Event " + eventType + "not recognized");
        }
    }

    //Parses the string to a shipment
    private Map<String,Integer> parseShipment(String shipment) {
        Map<String, Integer> allItems = new HashMap<>();
        String[] items = shipment.split(",");

        for (String s : items){
            String itemName = s.split("x")[0].trim();
            Integer amount = Integer.valueOf(s.split("x")[1].trim());

            allItems.put(itemName, amount);
        }

        return allItems;
    }

    //Parses the string to a valid Order object
    private Order parseOrder(String event){
        Integer tableNumber = Integer.valueOf(event.split(";")[0].trim());
        String[] items = event.split(";")[1].split(",");
        Order myOrder = new Order(tableNumber);

        for (String s : items){
            String foodItem = s.split("x")[0].trim();
            Integer amount = Character.getNumericValue(s.split("x")[1].trim().charAt(0));

            if (menu.containsKey(foodItem)){
                for (int i = 0; i < amount; i++){
                    Food toAdd = new Food(menu.get(foodItem));

                    if (s.contains("+")){
                        List<String> additions = parseChanges(s, '+');
                        for (String addition : additions){
                            toAdd.addIngredient(addition, 1);
                        }
                    } else if (s.contains("-")){
                        List<String> removals = parseChanges(s, '-');
                        for (String removal : removals){
                            toAdd.removeIngredient(removal, 1);
                        }
                    }

                    myOrder.addFood(toAdd);
                }
            }
        }

        return myOrder;
    }

    private List<String> parseChanges(String fullText, char change) {
        String[] allThings = fullText.split("\\s");
        ArrayList<String> items = new ArrayList<>();

        for (String s : allThings){
            if (!s.isEmpty() && s.charAt(0) == change){
                items.add(s.substring(1));
            }
        }

        return items;
    }

    //Gets the Server object from the list based on the serverID
    private Server getServer(String serverID){
        for (Server server : servers){
            if (serverID.equals(server.getID())){
                return server;
            }
        }
        throw new IllegalArgumentException("Server not found");
    }

    //Main loop that will read the events and do them
    public static void main(String[] args) {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server("server1"));
        servers.add(new Server("server2"));
        servers.add(new Server("server3"));

        List<Cook> cooks = new ArrayList<>();
        cooks.add(new Cook("cook1"));
        cooks.add(new Cook("cook2"));
        cooks.add(new Cook("cook3"));

        Restaurant mainRestaurant = new Restaurant(servers, cooks);
        mainRestaurant.processEvents(Restaurant.EVENT_FILE);
    }
}
