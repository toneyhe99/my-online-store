package my.online.store.spring5webapp.repositories;

import my.online.store.spring5webapp.domain.MyOrder;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jt on 12/23/19.
 */
public interface OrderRepository extends CrudRepository<MyOrder, Long> {
}
