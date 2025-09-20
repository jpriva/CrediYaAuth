package co.com.pragma.model.user.filters;

import lombok.*;

import java.math.BigDecimal;

/**
 * A domain object specifically for holding user search criteria.
 * This keeps the core User domain model clean from filter-specific fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserFilter {
    private String idNumber;
    private String email;
    private String name;
    private BigDecimal salaryLowerThan;
    private BigDecimal salaryGreaterThan;
}