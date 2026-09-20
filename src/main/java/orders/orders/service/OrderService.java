package orders.orders.service;

import orders.orders.model.Order;
import orders.orders.model.OrderStatus;
import orders.orders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> findAllOrders(Pageable pageable) {
        return this.orderRepository.findAll(pageable);
    }

    public Page<Order> findAllWithOrderItemsJoinFetch(Pageable pageable) {
        return this.orderRepository.findAllWithOrderItemsJoinFetch(pageable);
    }

    public Optional<Order> findOrderById(Long id) {
        return this.orderRepository.findById(id);
    }

    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return this.orderRepository.findByOrderStatus(status, pageable);
    }
}
