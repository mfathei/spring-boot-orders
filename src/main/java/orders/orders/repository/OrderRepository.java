package orders.orders.repository;

import orders.orders.model.Order;
import orders.orders.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * NOTE ON PAGINATION + JOIN FETCH:
     * Using 'join fetch' on a @OneToMany collection with Pageable causes Hibernate warning:
     * "HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!"
     * Hibernate must fetch ALL rows to RAM to paginate because SQL JOIN duplicates parent rows.
     * To paginate collections safely, prefer standard findAll(Pageable) combined with
     * @BatchSize(size = 25) on Order.orderItems.
     */
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

    /**
     * N+1 Solution (JPQL Join Fetch):
     * Fetches Order, its OrderItems, and the Product inside each OrderItem in a single SQL query.
     * This avoids the secondary N+1 query when item.getProduct() is called.
     */
    @Query("""
        select distinct o
        from Order o
        left join fetch o.orderItems oi
        left join fetch oi.product
        where o.id = :id
    """)
    Optional<Order> findByIdWithItemsAndProduct(@Param("id") Long id);

    /**
     * N+1 Solution (Spring Data @EntityGraph):
     * Declarative graph fetch specifying nested paths ("orderItems.product").
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdWithItemsAndProductEntityGraph(@Param("id") Long id);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
}
