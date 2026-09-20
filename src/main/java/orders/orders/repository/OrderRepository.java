package orders.orders.repository;

import orders.orders.model.Order;
import orders.orders.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // N+1 solution: Join Fetch
    @Query("""
        select distinct o
        from Order o
        left join fetch o.orderItems
    """)
    Page<Order> findAllWithOrderItemsJoinFetch(Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    @Query("""
        select distinct o
        from Order o
    """)
    Page<Order> findAllWithOrderItemsEntityGraph(Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
}
