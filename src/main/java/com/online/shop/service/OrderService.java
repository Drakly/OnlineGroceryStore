package com.online.shop.service;

import com.online.shop.dto.OrderRequestDto;
import com.online.shop.dto.OrderResponseDto;
import com.online.shop.entity.*;
import com.online.shop.repository.OrderRepository;
import com.online.shop.repository.ProductRepository;
import com.online.shop.repository.RouteStepRepository;
import com.online.shop.util.BotRouteCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RouteStepRepository routeStepRepository;
    private final BotRouteCalculator botRouteCalculator;

    public OrderService(OrderRepository orderRepository, 
                       ProductRepository productRepository,
                       RouteStepRepository routeStepRepository,
                       BotRouteCalculator botRouteCalculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.routeStepRepository = routeStepRepository;
        this.botRouteCalculator = botRouteCalculator;
    }

    public OrderResponseDto processOrder(User user, OrderRequestDto orderRequest) {
        Map<String, Integer> requestedItems = orderRequest.getItems();
        List<String> productNames = new ArrayList<>(requestedItems.keySet());
        List<Product> products = productRepository.findByNameIn(productNames);

        Set<String> foundProductNames = products.stream()
            .map(Product::getName)
            .collect(Collectors.toSet());
        
        List<String> missingProducts = productNames.stream()
            .filter(name -> !foundProductNames.contains(name))
            .collect(Collectors.toList());

        if (!missingProducts.isEmpty()) {
            return createFailedOrderResponse(null, "Products not found: " + String.join(", ", missingProducts), missingProducts);
        }

        List<String> insufficientStockItems = new ArrayList<>();
        Map<Product, Integer> productsToCollect = new HashMap<>();

        for (Product product : products) {
            Integer requestedQuantity = requestedItems.get(product.getName());
            if (!product.hasSufficientStock(requestedQuantity)) {
                insufficientStockItems.add(product.getName() + ": requested " + requestedQuantity + ", available " + product.getQuantity());
            } else {
                productsToCollect.put(product, requestedQuantity);
            }
        }

        if (!insufficientStockItems.isEmpty()) {
            return createFailedOrderResponse(null, 
                "We cannot fulfill your order right now – not enough stock. Missing items:\n* " + 
                String.join("\n* ", insufficientStockItems), 
                insufficientStockItems);
        }

        Order order = new Order(user, Order.OrderStatus.SUCCESS);
        order = orderRepository.save(order);

        for (Map.Entry<Product, Integer> entry : productsToCollect.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            OrderItem orderItem = new OrderItem(order, product, quantity);
            order.addOrderItem(orderItem);

            product.reduceQuantity(quantity);
            productRepository.save(product);
        }

        List<List<Integer>> route = botRouteCalculator.calculateOptimalRoute(productsToCollect);
        saveRouteSteps(order, route);

        OrderResponseDto response = new OrderResponseDto(order.getId(), order.getStatus(), order.getCreatedAt());
        response.setVisitedLocations(route);
        response.setMessage("Order SUCCESS");

        return response;
    }

    private void saveRouteSteps(Order order, List<List<Integer>> route) {
        for (int i = 0; i < route.size(); i++) {
            List<Integer> coordinate = route.get(i);
            RouteStep routeStep = new RouteStep(order, i, coordinate.get(0), coordinate.get(1));
            order.addRouteStep(routeStep);
        }
        orderRepository.save(order);
    }

    private OrderResponseDto createFailedOrderResponse(UUID orderId, String message, List<String> missingItems) {
        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(orderId);
        response.setStatus(Order.OrderStatus.FAIL);
        response.setMessage(message);
        response.setMissingItems(missingItems);
        return response;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        OrderResponseDto response = new OrderResponseDto(order.getId(), order.getStatus(), order.getCreatedAt());
        
        if (order.getStatus() == Order.OrderStatus.SUCCESS) {
            List<RouteStep> routeSteps = routeStepRepository.findByOrderIdOrderByStepIndexAsc(orderId);
            List<List<Integer>> visitedLocations = routeSteps.stream()
                .map(step -> Arrays.asList(step.getX(), step.getY()))
                .collect(Collectors.toList());
            response.setVisitedLocations(visitedLocations);
            response.setMessage("Order SUCCESS");
        } else {
            response.setMessage("Order FAIL");
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<List<Integer>> getOrderRoute(UUID orderId) {
        List<RouteStep> routeSteps = routeStepRepository.findByOrderIdOrderByStepIndexAsc(orderId);
        return routeSteps.stream()
            .map(step -> Arrays.asList(step.getX(), step.getY()))
            .collect(Collectors.toList());
    }
} 