import java.util.*;

class FlashSaleManager {

    private HashMap<String, Integer> stock = new HashMap<>();

    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();


    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedHashMap<>());
    }


    public int checkStock(String productId) {

        if (!stock.containsKey(productId)) {
            System.out.println("Product not found");
            return 0;
        }

        int available = stock.get(productId);
        System.out.println(productId + " → " + available + " units available");
        return available;
    }


    public synchronized void purchaseItem(String productId, int userId) {

        if (!stock.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }

        int currentStock = stock.get(productId);

        if (currentStock > 0) {

            stock.put(productId, currentStock - 1);

            System.out.println("User " + userId +
                    " purchase SUCCESS. Remaining stock: " + (currentStock - 1));

        } else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
            int position = queue.size() + 1;

            queue.put(userId, position);

            System.out.println("User " + userId +
                    " added to waiting list. Position #" + position);
        }
    }


    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        System.out.println("Waiting list for " + productId);

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() +
                    " → Position " + entry.getValue());
        }
    }
}


public class PS02 {

    public static void main(String[] args) {

        FlashSaleManager manager = new FlashSaleManager();

        manager.addProduct("IPHONE15_256GB", 3);

        manager.checkStock("IPHONE15_256GB");

        manager.purchaseItem("IPHONE15_256GB", 12345);
        manager.purchaseItem("IPHONE15_256GB", 67890);
        manager.purchaseItem("IPHONE15_256GB", 11111);

        manager.purchaseItem("IPHONE15_256GB", 99999);
        manager.purchaseItem("IPHONE15_256GB", 88888);

        manager.showWaitingList("IPHONE15_256GB");
    }
}