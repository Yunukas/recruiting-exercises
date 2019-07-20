package com.deliverr;

import com.deliverr.InventoryAllocator;
import com.deliverr.OrderResult;
import com.deliverr.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryAllocatorTest {
    // Main object
    private InventoryAllocator allocator = new InventoryAllocator();

    // Order object
    private Map<String, Integer> orders = new HashMap<>();

    // List of com.deliverr.Warehouse objects
    private List<Warehouse> warehouses = new ArrayList<>();

    // actual result object
    private OrderResult result = new OrderResult();

    // expected result object
    private OrderResult expected = new OrderResult();

    @Test
    void test1() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:2,orange:1");
        warehouseGenerator("owd", "apple:2,orange:1");
        warehouseGenerator("dm", "apple:1,orange:1");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();
        expected.add(expectedGenerator("owd", "apple:2,orange:1"));

        assertEquals(expected.getResult(), result.getResult());
    }

    @Test
    void test2() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:9,orange:4");
        warehouseGenerator("owd", "apple:5,orange:3");
        warehouseGenerator("dm", "apple:4,orange:1");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();
        expected.add(expectedGenerator("owd", "apple:5,orange:3"));
        expected.add(expectedGenerator("dm", "apple:4,orange:1"));

        assertEquals(expected.getResult(), result.getResult());
    }

    @Test
    void test3() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:4,orange:2");
        warehouseGenerator("owd", "apple:1,orange:3");
        warehouseGenerator("dm", "apple:4,orange:2");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();
        expected.add(expectedGenerator("dm", "apple:4,orange:2"));

        assertEquals(expected.getResult(), result.getResult());
    }

    @Test
    void test4() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:5,orange:5");
        warehouseGenerator("owd", "apple:0,orange:5");
        warehouseGenerator("dm", "apple:0,orange:5");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();

        assertEquals(expected.getResult(), result.getResult());
    }

    @Test
    void test5() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:-1,orange:0");
        warehouseGenerator("owd", "apple:30,orange:0");
        warehouseGenerator("dm", "apple:0,orange:10");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();

        assertEquals(expected.getResult(), result.getResult());
    }

    @Test
    void test6() {
        orders.clear();
        warehouses.clear();

        orderGenerator("apple:0,orange:0");
        warehouseGenerator("owd", "apple:10,orange:10");
        warehouseGenerator("dm", "apple:10,orange:10");

        // GET THE ACTUAL RESULT
        result = allocator.processOrder(orders, warehouses);

        // construct the expected object
        expected = new OrderResult();

        assertEquals(expected.getResult(), result.getResult());
    }

    // Generate an order object and add to the orders map
    private void orderGenerator(String order) {
        // order format "apple:5,orange:10"
        String[] splitItems = order.split(",");
        for (String item : splitItems) {
            String[] itemToAmount = item.split(":");
            orders.put(itemToAmount[0], Integer.parseInt(itemToAmount[1]));
        }
    }

    // Generate a com.deliverr.Warehouse object and add to the list of com.deliverr.Warehouse objects
    private void warehouseGenerator(String name, String inventory) {
        Warehouse wh = new Warehouse(name);
        // inventory format "apple:5,orange:10"
        String[] splitItems = inventory.split(",");
        for (String item : splitItems) {
            String[] itemToAmount = item.split(":");
            wh.addItem(itemToAmount[0], Integer.parseInt(itemToAmount[1]));
        }
        warehouses.add(wh);
    }

    // Generate an expected result object with given items, ex: ("owd", "apple:5,orange:10")
    private Map<String, Map<String, Integer>> expectedGenerator(String warehouse, String orderResult) {
        Map<String, Integer> expectedOrders = new HashMap<>();
        // orderResult format "apple:5,orange:10"
        String[] splitItems = orderResult.split(",");
        for (String item : splitItems) {
            String[] itemToAmount = item.split(":");
            expectedOrders.put(itemToAmount[0], Integer.parseInt(itemToAmount[1]));
        }
        Map<String, Map<String, Integer>> result = new HashMap<>();
        result.put(warehouse, expectedOrders);
        return result;
    }
}