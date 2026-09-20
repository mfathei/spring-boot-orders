package orders.orders.controller;

import orders.orders.model.Order;
import orders.orders.model.OrderStatus;
import orders.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public Page<Order> findAllOrders(@PageableDefault Pageable pageable) {
        return this.orderService.findAllOrders(pageable);
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable Long id) {
        Optional<Order> o = this.orderService.findOrderById(id);
        return o.map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("with-items")
    public Page<Order> findAllOrdersWithItems(@PageableDefault Pageable pageable) {
        return this.orderService.findAllWithOrderItemsJoinFetch(pageable);
    }

    @GetMapping("by-status/{status}")
    public Page<Order> findAllOrdersByStatus(@PathVariable OrderStatus status, @PageableDefault Pageable pageable) {
        return this.orderService.findByStatus(status, pageable);
    }
}
