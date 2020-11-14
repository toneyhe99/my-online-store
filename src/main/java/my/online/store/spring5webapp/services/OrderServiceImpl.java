package my.online.store.spring5webapp.services;

import my.online.store.spring5webapp.domain.MyOrder;
import my.online.store.spring5webapp.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public MyOrder saveOrUpdateOrder(MyOrder o) {
        return orderRepository.save(o);
    }

    public Iterable<MyOrder> findAll(){
        return orderRepository.findAll();
    }

    public Optional<MyOrder> findById(Long orderId){
        return orderRepository.findById(orderId);
    }
}
