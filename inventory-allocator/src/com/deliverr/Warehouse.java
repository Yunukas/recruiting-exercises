package com.deliverr;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    // name of warehouse
    private String name;
    // amount of each item, ex: {apple: 5, orange: 10}
    private Map<String, Integer> itemToAmount;
    // the items that are shipped from this inventory and their amounts
    private Map<String, Integer> itemsToShip;
    // this will confirm if there are items are being shipped from this warehouse
    private boolean isShipping;

    // construct the warehouse with name and initialize the members
    public Warehouse(String name) {
        this.name = name;
        itemToAmount = new HashMap<>();
        itemsToShip = new HashMap<>();
        isShipping = false;
    }

    // add item and amount to the map
    public void addItem(String item, int amount) {
        itemToAmount.put(item, amount);
    }

    // return the warehouse name
    public String getName() {
        return name;
    }

    // return amount of a specific item, ex: apple -> 5
    private int getItemAmount(String item) {
        if (itemToAmount.containsKey(item))
            return itemToAmount.get(item);

        return 0;
    }

    // check if one warehouse can fulfill the total order
    public boolean canFulfillTotalOrder(Map<String, Integer> order) {
        // loop through each item and if order amount
        // is greater than inventory, return false
        for (Map.Entry<String, Integer> orderPiece : order.entrySet()) {
            if (orderPiece.getValue() > getItemAmount(orderPiece.getKey()))
                return false;
        }
        return true;
    }

    // this method will return the shipped items and their amounts
    public Map<String, Map<String, Integer>> getShippingInfo() {
        Map<String, Map<String, Integer>> shippingInfo = new HashMap<>();
        if (isShipping) {
            shippingInfo.put(name, itemsToShip);
            return shippingInfo;
        }
        return null;
    }

    // check inventory and get the amount
    // of items that can be shipped
    public int shipItem(String item, int amount) {
        if (itemToAmount.containsKey(item)) {
            if (itemToAmount.get(item) > 0) {
                // whichever amount is less will be the deciding factor
                int toBeShipped = Math.min(amount, itemToAmount.get(item));
                // mark this inventory as shipping
                isShipping = true;
                // add the shipped item and its amount to the list
                itemsToShip.put(item, toBeShipped);
                return toBeShipped;
            }
        }
        // if item couldnt be found or the amount is zero
        return 0;
    }
}

