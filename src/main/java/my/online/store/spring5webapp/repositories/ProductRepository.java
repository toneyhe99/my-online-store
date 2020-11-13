package my.online.store.spring5webapp.repositories;

import my.online.store.spring5webapp.domain.Product;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jt on 12/23/19.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
}
