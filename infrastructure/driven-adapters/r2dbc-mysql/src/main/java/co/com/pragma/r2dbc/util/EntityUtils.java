package co.com.pragma.r2dbc.util;

import co.com.pragma.model.user.filters.UserFilter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EntityUtils {

    public static final String USER_TABLE_NAME = "Usuario";
    public static final String ID_NUMBER_COLUMN_NAME = "documento_identidad";
    public static final String NAME_COLUMN_NAME = "nombre";
    public static final String LAST_NAME_COLUMN_NAME = "apellido";
    public static final String EMAIL_COLUMN_NAME = "email";
    public static final String SALARY_COLUMN_NAME = "salario_base";

    public static final String ROLE_TABLE_NAME = "Rol";


    public static String addWildcard(String text) {
        return "%" + text + "%";
    }

    public static Criteria buildCriteria(UserFilter filter) {
        Criteria criteria = Criteria.empty();

        if (StringUtils.hasText(filter.getIdNumber())) {
            criteria = criteria.and(EntityUtils.ID_NUMBER_COLUMN_NAME).like(EntityUtils.addWildcard(filter.getIdNumber())).ignoreCase(true);
        }
        if (StringUtils.hasText(filter.getName())) {
            String[] nameParts = filter.getName().split("\\s+");
            for (String part : nameParts) {
                Criteria namePartCriteria = Criteria.where(EntityUtils.NAME_COLUMN_NAME).like(EntityUtils.addWildcard(part)).ignoreCase(true)
                        .or(EntityUtils.LAST_NAME_COLUMN_NAME).like(EntityUtils.addWildcard(part)).ignoreCase(true);
                criteria = criteria.and(namePartCriteria);
            }
        }
        if (StringUtils.hasText(filter.getEmail())) {
            criteria = criteria.and(EntityUtils.EMAIL_COLUMN_NAME).like(EntityUtils.addWildcard(filter.getEmail())).ignoreCase(true);
        }
        if (filter.getSalaryGreaterThan() != null) {
            criteria = criteria.and(EntityUtils.SALARY_COLUMN_NAME).greaterThan(filter.getSalaryGreaterThan());
        }
        if (filter.getSalaryLowerThan() != null) {
            criteria = criteria.and(EntityUtils.SALARY_COLUMN_NAME).lessThan(filter.getSalaryLowerThan());
        }
        return criteria;
    }

}
