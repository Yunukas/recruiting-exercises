package com.deliverr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAllocator {
    // Order items and their amounts
    private Map<String, Integer> order = new HashMap<>();
    // list of warehouses and their inventory
    private List<Warehouse> warehouseList = new ArrayList<>();
    // result of the order whether it can be fulfilled or not
    private boolean orderFulfilled = true;

    // pre-validation before proceeding with the order
    private boolean validateOrderAmounts(Map<String, Integer> order){
        // if there are negative amounts, return empty result, ex: apple:-1,orange:5
        boolean negativeAmount = false;
        for (int amount : order.values()) {
            if (amount < 0){
                negativeAmount = true;
                break;
            }
        }
        // check if there is at least one item with
        // amount > 0 (preventing against a fake order of all 0s)
        boolean nonZero = false;
        for (int amount : order.values()) {
            if (amount > 0){
                nonZero = true;
                break;
            }
        }
        // if validation failed, return false
        if (!nonZero || negativeAmount){
            return false;
        }
        return true;
    }

    // this public method will orchestrate the order process
    public OrderResult processOrder(Map<String, Integer> order, List<Warehouse> warehouseList) {
        // check if there are negative numbers or
        // all numbers are zero
        if(!validateOrderAmounts(order)){
            System.out.println("Not a legit order!\n");
            return new OrderResult();
        }
        // assign members
        this.order = order;
        this.warehouseList = warehouseList;

        // check if the order can be fulfilled by a single warehouse
        // which is expected to be the cheapest option
        // a positive number points to the index of the warehouse
        int designatedWarehouse = canBeFulfilledBySingleWarehouse();
        if (designatedWarehouse >= 0) {
            System.out.println(
            "All of the order will be shipped from: "
            + warehouseList.get(designatedWarehouse).getName());
            // call single location shipping
            shipFromSingleWarehouse(warehouseList.get(designatedWarehouse));
        }
        else
            shipFromMultipleWarehouses();

        return collectResult(orderFulfilled);
    }

    // this method handles the shipping from the designated warehouse
    private void shipFromSingleWarehouse(Warehouse warehouse) {
        for (Map.Entry<String, Integer> orderPiece : order.entrySet()) {
            warehouse.shipItem(orderPiece.getKey(), orderPiece.getValue());
        }
    }

    // When order has to be split between different warehouses
    private void shipFromMultipleWarehouses() {
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
            // an ordered item is more than the inventory amount
            if (amount > 0) {
                orderFulfilled = false;
                break;
            }
        }
        if (orderFulfilled)
            System.out.println("Order will be split between warehouses");
    }

    // this method will check if there is a warehouse
    // that can single-handedly ship the order
    private int canBeFulfilledBySingleWarehouse() {
        // if order can be filled by a single name, return its index
        for (int i = 0; i < warehouseList.size(); i++) {
            if (warehouseList.get(i).canFulfillTotalOrder(order))
                return i;
        }
        // else return -1
        return -1;
    }

    // collect the result of shipping -> warehouse name, shipped items and their amounts
    private OrderResult collectResult(boolean orderFulfilled) {
        OrderResult orderResult = new OrderResult();
        // if order was fulfilled,
        // get shipping info from each inventory
        if (orderFulfilled) {
            for (Warehouse warehouse : warehouseList) {
                if (warehouse.getShippingInfo() != null)
                    orderResult.add(warehouse.getShippingInfo());
            }
        }
        // print result on screen
        printResult(orderResult);
        return orderResult;
    }
    // this method will print the order result on screen
    private void printResult(OrderResult orderResult){
        System.out.print("[");
        for (Map<String, Map<String, Integer>> map : orderResult.getResult()) {

            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                System.out.print("{" + entry.getKey() + ": {");
                for (Map.Entry<String, Integer> items : entry.getValue().entrySet()) {
                    System.out.print(" " + items.getKey() + ": " + items.getValue() + ", ");
                }
                System.out.print(" },");
            }
        }
        System.out.print("]\n\n");
    }
}


