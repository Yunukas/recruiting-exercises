package com.deliverr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderResult {
    private List<Map<String, Map<String, Integer>>> result = new ArrayList<>();

    public void add(Map<String, Map<String, Integer>> order) {
        result.add(order);
    }

    public List<Map<String, Map<String, Integer>>> getResult() {
        return result;
    }
}
