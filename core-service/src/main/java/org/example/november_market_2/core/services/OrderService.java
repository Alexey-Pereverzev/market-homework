package org.example.november_market_2.core.services;

import lombok.RequiredArgsConstructor;
import org.example.november_market_2.api.LineItemDto;
import org.example.november_market_2.core.entities.Order;
import org.example.november_market_2.core.entities.OrderItem;
import org.example.november_market_2.core.integrations.CartServiceIntegration;
import org.example.november_market_2.core.repositories.OrderItemRepository;
import org.example.november_market_2.core.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderItemService orderItemService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartServiceIntegration cartServiceIntegration;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    public List<OrderItem> findItemsByOrderId(Long id) {
        return orderItemRepository.findByOrderId(id);
    }
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public void createNewOrder(String username, String phoneNumber, String address) {
        List<LineItemDto> lineItemDtos = cartServiceIntegration.getCurrentUserCart(username).getItems();
        if (lineItemDtos.isEmpty()){
            return;
        }
        Order order = new Order();
        order.setTotalPrice(BigDecimal.ZERO);
        order.setUsername(username);
        order.setPhoneNumber(phoneNumber);
        order.setAddress(address);
        for (LineItemDto lineItemDto : lineItemDtos) {
            OrderItem item = orderItemService.createFromLineItem(lineItemDto);
            item.setOrder(order);
            order.getItems().add(item);
            order.setTotalPrice(order.getTotalPrice().add(item.getPrice()));
        }
        orderRepository.save(order);
    }

    public List<Order> findUserOrders(String username) {
        return orderRepository.findAllByUsername(username);
    }
}
