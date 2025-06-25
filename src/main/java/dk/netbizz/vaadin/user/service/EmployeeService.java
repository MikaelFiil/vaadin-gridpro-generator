package dk.netbizz.vaadin.user.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class EmployeeService {


    private final JdbcTemplate jdbcTemplate;

    public EmployeeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ApplicationUser applicationUser) {
        ServicePoint.servicePointInstance().getEmployeeRepository().save(applicationUser);
    }

    @Transactional
    public ApplicationUser findById(Integer id) {
        ApplicationUser applicationUser = ServicePoint.servicePointInstance().getEmployeeRepository().findById(id).orElse(null);
        getApplicationUserItems(applicationUser);
        return applicationUser;
    }


    @Transactional
    public List<ApplicationUser> findByTenantDepartmentId(Integer id) {
        List<ApplicationUser> applicationUserList =  ServicePoint.servicePointInstance().getEmployeeRepository().findByTenantDepartmentId(id);
        for(ApplicationUser applicationUser : applicationUserList) {
            getApplicationUserItems(applicationUser);
        }
        return applicationUserList;
    }

    private void getApplicationUserItems(ApplicationUser user) {
        // user.setItems(ServicePoint.getInstance().getItemRepository().findByApplicationUserId(user.getId()));
    }

    @Transactional
    public List<ApplicationUser> findFromQuery(String where, String orderBy, int limit, int offset) {
        return jdbcTemplate.query("select * from application_user" + where + orderBy + " limit " + limit + " offset " + offset,
                new EmployeeService.ApplicationUserMapper());
    }

    @Transactional
    public Integer countFromQueryFilter(String where) {
        return jdbcTemplate.queryForObject("select count(*) as rowCount from application_user" + where, null, Integer.class);
    }

    private static final class ApplicationUserMapper implements RowMapper<dk.netbizz.vaadin.user.domain.ApplicationUser> {

        public ApplicationUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            ApplicationUser user = new ApplicationUser();
            user.setId(rs.getInt("id"));
            user.setVersion(rs.getInt("version"));
            user.setTenantDepartmentId(rs.getInt("tenant_department_id"));

            user.setFullname(rs.getString("fullname"));
            user.setMustChangePwd(rs.getBoolean("must_change_pwd"));
            user.setEmail(rs.getString("email"));
            user.setEmailConfirmed(rs.getBoolean("email_confirmed"));
            user.setEmailConfirmationString(rs.getString("email_confirmation_string"));
            if (rs.getDate("birthday") != null) {
                user.setBirthday(rs.getDate("birthday").toLocalDate());
            }
            user.setPhone(rs.getString("phone"));
            user.setCreated(rs.getTimestamp("created").toLocalDateTime());
            user.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
            user.setIsLocked(rs.getBoolean("is_locked"));
            user.setIsDisabled(rs.getBoolean("is_disabled"));
            user.setPicture(rs.getBytes("picture"));
            user.setDescription(rs.getString("description"));
            return user;
        }
    }

}

