package dk.netbizz.vaadin.item.service;

import dk.netbizz.vaadin.exception.ApplicationRuntimeException;
import dk.netbizz.vaadin.item.domain.Item;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Repository
public class ItemRepository {

    String sqlInsert = """
        insert into item (application_user_id, item_name, category, kr_per_liter, price, warehouse_id, birthday, active, criticality, description, yearly_amount, impact_amount, likelihood)
        values(:application_user_id, :item_name, :category, :kr_per_liter, :price, :warehouse_id, :birthday, :active, :criticality, :description, :yearly_amount, :impact_amount, :likelihood)
        """;

    String sqlUpdate = """
        update item set application_user_id = :application_user_id, item_name = :item_name, category = :category, kr_per_liter = :kr_per_liter, price = :price, warehouse_id = :warehouse_id,
        birthday = :birthday, active = :active, criticality = :criticality, description = :description, yearly_amount = :yearly_amount, impact_amount = :impact_amount, likelihood = :likelihood
        where id = :id
        """;

    private final JdbcClient jdbcClient;

    public ItemRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Transactional(rollbackFor = { SQLException.class, ApplicationRuntimeException.class })
    public List<Item> findByApplicationUserId(Integer id) {
        String sql = "select * from item where application_user_id = :id";

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(ItemRowMapper.getInstance())
                .list();
    }


    @Transactional(rollbackFor = { SQLException.class, ApplicationRuntimeException.class })
    public Item save(Item item) {
        JdbcClient.StatementSpec spec;
        int rowCount = 0;

        if (item.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            spec = getStatementSpec(item, sqlInsert);
            rowCount = spec.update(keyHolder, "id");
            // System.out.println("Inserted " + rowCount + " rows");
            item.setId(keyHolder.getKey().intValue());
            return item;
        } else {
            spec = getStatementSpec(item, sqlUpdate);
            rowCount = spec.update();
            // System.out.println("Inserted " + rowCount + " rows");
            return item;
        }
    }

    @Transactional(rollbackFor = { SQLException.class, ApplicationRuntimeException.class })
    public boolean delete(Item item) {
        String sql = """
        delete from item where id = :id
        """;

        int rowCount = jdbcClient.sql(sql)
           .param("id", item.getId())
            .update();

        return rowCount == 1;
    }

    private JdbcClient.StatementSpec getStatementSpec(Item item, String sql) {
        return jdbcClient.sql(sql)
            .param("id", item.getId())
            .param("application_user_id", item.getApplicationUserId())
            .param("item_name", item.getItemName())
            .param("category", item.getCategory())
            .param("kr_per_liter", item.getKrPerLiter())
            .param("price", item.getPrice())
            .param("warehouse_id", (item.getWarehouse() != null ? item.getWarehouse().getId() : null))
            .param("birthday", item.getBirthday())
            .param("active", item.getActive())
            .param("criticality", item.getCriticality())
            .param("description", item.getDescription())
            .param("yearly_amount", item.getYearlyAmount())
            .param("impact_amount", item.getImpactAmount())
            .param("likelihood", item.getLikelihood());
    }

}

