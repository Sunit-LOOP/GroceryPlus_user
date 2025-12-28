package com.sunit.groceryplus.utils;

import java.util.*;

/**
 * Advanced Delivery Optimization Utility
 * implements Dijkstra's Algorithm and Nearest Neighbor Heuristic
 */
public class DeliveryOptimizer {

    // Simulated Graph for Delivery Network (Location Name -> Map of Neighbors with distance/time)
    private static final Map<String, Map<String, Integer>> deliveryGraph = new HashMap<>();

    static {
        // Initialize simulated map of a city area
        addRoute("Store", "Area A", 5);
        addRoute("Store", "Area B", 8);
        addRoute("Area A", "Area C", 4);
        addRoute("Area B", "Area C", 2);
        addRoute("Area B", "Area D", 10);
        addRoute("Area C", "Area D", 5);
        addRoute("Area C", "Area E", 7);
        addRoute("Area D", "Area E", 3);
    }

    private static void addRoute(String from, String to, int time) {
        deliveryGraph.computeIfAbsent(from, k -> new HashMap<>()).put(to, time);
        deliveryGraph.computeIfAbsent(to, k -> new HashMap<>()).put(from, time);
    }

    /**
     * Dijkstra's Algorithm: Find the shortest path (time) from store to a location
     * @param targetLocation The name of the destination area
     * @return Estimated delivery time in minutes
     */
    public static int calculateShortestDeliveryTime(String targetLocation) {
        String startNode = "Store";
        if (!deliveryGraph.containsKey(targetLocation)) return 15; // Default if area not in graph

        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Set<String> visited = new HashSet<>();

        for (String node : deliveryGraph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);
        pq.add(new Node(startNode, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (!visited.add(current.name)) continue;

            if (current.name.equals(targetLocation)) return current.distance;

            for (Map.Entry<String, Integer> neighbor : deliveryGraph.get(current.name).entrySet()) {
                int newDist = distances.get(current.name) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    pq.add(new Node(neighbor.getKey(), newDist));
                }
            }
        }

        return 20; // Default fallback
    }

    /**
     * Nearest Neighbor Heuristic: Optimize route sequence for multiple orders
     * This is a simple solution to the Traveling Salesman Problem (TSP)
     * @param locations List of delivery locations
     * @return Optimized sequence of locations
     */
    public static List<String> optimizeRoutePath(List<String> locations) {
        if (locations == null || locations.isEmpty()) return new ArrayList<>();

        List<String> optimizedPath = new ArrayList<>();
        String current = "Store";
        List<String> remaining = new ArrayList<>(locations);

        while (!remaining.isEmpty()) {
            String next = null;
            int minTime = Integer.MAX_VALUE;

            for (String loc : remaining) {
                int time = getDirectTime(current, loc);
                if (time < minTime) {
                    minTime = time;
                    next = loc;
                }
            }

            if (next != null) {
                optimizedPath.add(next);
                remaining.remove(next);
                current = next;
            } else {
                break;
            }
        }

        return optimizedPath;
    }

    private static int getDirectTime(String from, String to) {
        if (from.equals(to)) return 0;
        if (deliveryGraph.containsKey(from) && deliveryGraph.get(from).containsKey(to)) {
            return deliveryGraph.get(from).get(to);
        }
        return 10; // Default estimated direct time
    }

    /**
     * Suggest the best delivery person based on proximity to the store or delivery location
     * @param targetArea The area where order needs to be delivered
     * @param personnel List of available delivery personnel
     * @return The suggested delivery person
     */
    public static com.sunit.groceryplus.models.DeliveryPerson getBestDeliveryPerson(String targetArea, List<com.sunit.groceryplus.models.DeliveryPerson> personnel) {
        if (personnel == null || personnel.isEmpty()) return null;

        com.sunit.groceryplus.models.DeliveryPerson best = null;
        int minTime = Integer.MAX_VALUE;

        // Mock locations for personnel (in a real app, these come from DB or GPS)
        Map<Integer, String> mockLocations = new HashMap<>();
        for (int i = 0; i < personnel.size(); i++) {
            String area = (i % 2 == 0) ? "Area A" : "Area C";
            mockLocations.put(personnel.get(i).getPersonId(), area);
        }

        for (com.sunit.groceryplus.models.DeliveryPerson p : personnel) {
            if (!"Available".equalsIgnoreCase(p.getStatus())) continue;

            String currentArea = mockLocations.getOrDefault(p.getPersonId(), "Store");
            int timeToOrder = calculateShortestDeliveryTime(currentArea); // Time to reach store or location
            
            if (timeToOrder < minTime) {
                minTime = timeToOrder;
                best = p;
            }
        }

        return best;
    }

    private static class Node {
        String name;
        int distance;
        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}
