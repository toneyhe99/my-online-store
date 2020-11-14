package my.online.store.spring5webapp.services;

import my.online.store.spring5webapp.domain.MyOrder;

import java.util.Optional;

public interface OrderService {
    MyOrder saveOrUpdateOrder(MyOrder o);

    Iterable<MyOrder> findAll();

    Optional<MyOrder> findById(Long orderId);
}
