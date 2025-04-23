package dk.netbizz.vaadin.gridpro.entity;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ApplicationUserRepository extends ListCrudRepository<ApplicationUser, Integer> {

    List<ApplicationUser> findByTenantDepartmentId(Integer postId);

}
