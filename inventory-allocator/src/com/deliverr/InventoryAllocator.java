package com.deliverr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAllocator {

    // result of the order whether it can be fulfilled or not
    private boolean orderFulfilled = true;

    // this public method will orchestrate the order process
    public OrderResult processOrder(Map<String, Integer> order, List<Warehouse> warehouseList) {
        // if there are negative amounts, return empty result, ex: apple:-1,orange:5
        for (int amount : order.values()) {
            if (amount < 0)
                return new OrderResult();
        }

        // check if there are non-zero amounts
        boolean nonZero = false;
        for (int amount : order.values()) {
            if (amount > 0)
                nonZero = true;
        }
        // if all item amounts are Zero, return empty result
        if (!nonZero)
            return new OrderResult();

        // check if the order can be fulfilled by a single warehouse
        // which is expected to be the cheapest option
        // a positive number points to the index of the warehouse
        int designatedWarehouse = canBeFulfilledBySingleWarehouse(order, warehouseList);

        if (designatedWarehouse >= 0) {
            System.out.println("All of the order will be shipped from: "
                    + warehouseList.get(designatedWarehouse).getName());
            shipFromSingleWareHouse(order, warehouseList.get(designatedWarehouse));
        } else {
            shipFromMultipleWareHouses(order, warehouseList);
        }
        return collectResult(warehouseList, orderFulfilled);
    }

    // this method handles the shipping from the designated warehouse
    private void shipFromSingleWareHouse(Map<String, Integer> order, Warehouse warehouse) {
        for (Map.Entry<String, Integer> orderPiece : order.entrySet()) {
            warehouse.shipItem(orderPiece.getKey(), orderPiece.getValue());
        }
    }

    // if the order has to be split between different warehouses, this method will be called
    private void shipFromMultipleWareHouses(Map<String, Integer> order, List<Warehouse> warehouseList) {

        for (Map.Entry<String, Integer> orderPiece : order.entrySet()) {
            String item = orderPiece.getKey();      // current item, ex: orange
            int amount = orderPiece.getValue();     // ordered amount, ex: 5
            int currentInventory = 0;               // index of current name
            while (amount > 0 && currentInventory < warehouseList.size()) {
                // process current name, get shipped amount and increment to the next
                int shippableAmount = warehouseList.get(currentInventory++).shipItem(item, amount);
                amount -= shippableAmount;             // deduct the fulfilled amount
            }
            // this condition is met when the total amount of
            // an ordered item is more than inventory
            if (amount > 0) {
                orderFulfilled = false;
                break;
            }
        }
        if (orderFulfilled)
            System.out.println("Order will be split between warehouses");
    }

    // this method will check each warehouse if there is a single warehouse
    // that can ship all items at once
    private int canBeFulfilledBySingleWarehouse(Map<String, Integer> order, List<Warehouse> warehouseList) {
        // if order can be filled by a single name, return its index
        for (int i = 0; i < warehouseList.size(); i++) {
            if (warehouseList.get(i).canFulfillTotalOrder(order))
                return i;
        }
        // else return -1
        return -1;
    }

    // collect the result of shipping -> warehouse name, shipped items and their amounts
    private OrderResult collectResult(List<Warehouse> inventory, boolean orderFulfilled) {

        OrderResult orderResult = new OrderResult();
        // if order was fulfilled,
        // get shipping info from each inventory
        if (orderFulfilled) {
            for (Warehouse warehouse : inventory) {
                if (warehouse.getShippingInfo() != null)
                    orderResult.add(warehouse.getShippingInfo());
            }
        }

        // print results on screen
        System.out.print("[");
        for (Map<String, Map<String, Integer>> map : orderResult.getResult()) {

            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                System.out.print("{" + entry.getKey() + ": ");
                for (Map.Entry<String, Integer> items : entry.getValue().entrySet()) {
                    System.out.print("{ " + items.getKey() + ": " + items.getValue() + " } ");
                }
                System.out.print("}");
            }
        }
        System.out.print("]\n\n");

        return orderResult;
    }
}

class Warehouse {
    // name of name
    private String name;
    // amount of each item, ex: {apple: 5, orange: 10}
    private Map<String, Integer> itemToAmount;
    // the items that are shipped from this inventory and their amounts
    private Map<String, Integer> itemsToShip;

    // this will confirm if there are items are being shipped from this warehouse
    private boolean isShipping;

    // construct the name with name and initialize the members
    Warehouse(String name) {
        this.name = name;
        itemToAmount = new HashMap<>();
        itemsToShip = new HashMap<>();
        isShipping = false;
    }

    // add item and amount to the map
    void addItem(String item, int amount) {
        itemToAmount.put(item, amount);
    }

    // return the name name
    String getName() {
        return name;
    }

    // return amount of a specific item, ex: apple -> 5
    private int getItemAmount(String item) {
        if (itemToAmount.containsKey(item))
            return itemToAmount.get(item);

        return 0;
    }

    // check if one warehouse can fulfill the total order
    boolean canFulfillTotalOrder(Map<String, Integer> order) {
        // loop through each item and if order amount
        // is greater than inventory, return false
        for (Map.Entry<String, Integer> orderPiece : order.entrySet()) {
            if (orderPiece.getValue() > getItemAmount(orderPiece.getKey()))
                return false;
        }
        return true;
    }

    // this method will return the shipped items and their amounts
    Map<String, Map<String, Integer>> getShippingInfo() {
        Map<String, Map<String, Integer>> shippingInfo = new HashMap<>();
        if (isShipping) {
            shippingInfo.put(name, itemsToShip);
            return shippingInfo;
        }
        return null;
    }

    //
    int shipItem(String item, int amount) {
        if (itemToAmount.containsKey(item)) {
            if (itemToAmount.get(item) > 0) {
                // whichever amount is less will be the deciding factor
                int toBeShipped = Math.min(amount, itemToAmount.get(item));
                // decrement the inventory items
                itemToAmount.put(item, itemToAmount.get(item) - toBeShipped);
                // increment the amount of total shipped items
                isShipping = true;
                // add the shipped item and its amount to the list
                itemsToShip.put(item, toBeShipped);
                return toBeShipped;
            }
        }
        // if we couldnt find the item or the amount is zero
        return 0;
    }
}

class OrderResult {
    private List<Map<String, Map<String, Integer>>> result = new ArrayList<>();

    void add(Map<String, Map<String, Integer>> order) {
        result.add(order);
    }

    List<Map<String, Map<String, Integer>>> getResult() {
        return result;
    }
}
