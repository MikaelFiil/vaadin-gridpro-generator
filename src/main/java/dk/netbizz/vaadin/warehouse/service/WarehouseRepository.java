package dk.netbizz.vaadin.warehouse.service;

import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Transactional is default for all methods
@Repository
public interface WarehouseRepository extends ListCrudRepository<Warehouse, Integer> {

    Optional<Warehouse> findByWarehouseNameIgnoreCase(String name);
}
