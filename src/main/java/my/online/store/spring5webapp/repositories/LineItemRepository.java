package my.online.store.spring5webapp.repositories;

import my.online.store.spring5webapp.domain.LineItem;
import org.springframework.data.repository.CrudRepository;

public interface LineItemRepository extends CrudRepository<LineItem, Long> {
}
