package com.online.shop.util;

import com.online.shop.entity.Product;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BotRouteCalculator {

    private static final int START_X = 0;
    private static final int START_Y = 0;


    public List<List<Integer>> calculateOptimalRoute(Map<Product, Integer> productsToCollect) {
        List<List<Integer>> route = new ArrayList<>();
        
        route.add(Arrays.asList(START_X, START_Y));
        
        if (productsToCollect.isEmpty()) {
            return route;
        }
        
        Set<List<Integer>> locationsToVisit = new HashSet<>();
        for (Product product : productsToCollect.keySet()) {
            locationsToVisit.add(Arrays.asList(product.getX(), product.getY()));
        }
        
        int currentX = START_X;
        int currentY = START_Y;
        
        while (!locationsToVisit.isEmpty()) {
            List<Integer> nearestLocation = findNearestLocation(currentX, currentY, locationsToVisit);
            
            List<List<Integer>> pathToLocation = calculatePathBetweenPoints(
                currentX, currentY, 
                nearestLocation.get(0), nearestLocation.get(1)
            );
            
            for (int i = 1; i < pathToLocation.size(); i++) {
                route.add(pathToLocation.get(i));
            }
            
            currentX = nearestLocation.get(0);
            currentY = nearestLocation.get(1);
            
            locationsToVisit.remove(nearestLocation);
        }
        
        if (currentX != START_X || currentY != START_Y) {
            List<List<Integer>> pathToStart = calculatePathBetweenPoints(
                currentX, currentY, START_X, START_Y
            );
            
            for (int i = 1; i < pathToStart.size(); i++) {
                route.add(pathToStart.get(i));
            }
        }
        
        return route;
    }
    

    private List<Integer> findNearestLocation(int currentX, int currentY, Set<List<Integer>> locations) {
        List<Integer> nearest = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (List<Integer> location : locations) {
            int distance = calculateManhattanDistance(currentX, currentY, location.get(0), location.get(1));
            if (distance < minDistance) {
                minDistance = distance;
                nearest = location;
            }
        }
        
        return nearest;
    }
    

    private int calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
    

    private List<List<Integer>> calculatePathBetweenPoints(int startX, int startY, int endX, int endY) {
        List<List<Integer>> path = new ArrayList<>();
        
        path.add(Arrays.asList(startX, startY));
        
        int currentX = startX;
        int currentY = startY;
        
        while (currentX != endX) {
            if (currentX < endX) {
                currentX++;
            } else {
                currentX--;
            }
            path.add(Arrays.asList(currentX, currentY));
        }
        
        while (currentY != endY) {
            if (currentY < endY) {
                currentY++;
            } else {
                currentY--;
            }
            path.add(Arrays.asList(currentX, currentY));
        }
        
        return path;
    }
    

    public String formatRouteForDisplay(List<List<Integer>> route) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bot Route: ");
        
        for (int i = 0; i < route.size(); i++) {
            List<Integer> coordinate = route.get(i);
            sb.append("[").append(coordinate.get(0)).append(",").append(coordinate.get(1)).append("]");
            
            if (i < route.size() - 1) {
                sb.append(" → ");
            }
        }
        
        return sb.toString();
    }
} 