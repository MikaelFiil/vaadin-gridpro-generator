package dk.netbizz.vaadin.item.service;


import dk.netbizz.vaadin.item.domain.Item;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends ListCrudRepository<Item, Integer> {

}

