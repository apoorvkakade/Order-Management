package com.sideproject.orderservice.service;

import com.sideproject.orderservice.dto.InventoryResponse;
import com.sideproject.orderservice.dto.OrderLineItemsDto;
import com.sideproject.orderservice.dto.OrderRequest;
import com.sideproject.orderservice.dto.OrderResponse;
import com.sideproject.orderservice.model.Order;
import com.sideproject.orderservice.model.OrderLineItems;
import com.sideproject.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest)
    {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                .map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                        .map(orderLineItem -> orderLineItem.getSkuCode())
                .toList();
        String inventoryServiceURI = "http://inventory-service/api/inventory";

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri(inventoryServiceURI, uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(inventoryResponse -> inventoryResponse.isInStock());

        if(allProductsInStock)
        {
            orderRepository.save(order);
            return "Order Placed successfully";
        }
        else
        {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(order->mapToOrderResponse(order)).collect(Collectors.toList());

    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderLineItems> orderLineItems = order.getOrderLineItemsList();
        List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<>();
        for(OrderLineItems orderLineItem:orderLineItems)
        {
            OrderLineItemsDto e= new OrderLineItemsDto();
            e.setPrice(orderLineItem.getPrice());
            e.setQuantity(orderLineItem.getQuantity());
            e.setSkuCode(orderLineItem.getSkuCode());
            e.setId(orderLineItem.getId());
            orderLineItemsDtoList.add(e);
        }

        return OrderResponse.builder()
                .orderLineItemsDtoList(orderLineItemsDtoList)
                .build();
    }
}
