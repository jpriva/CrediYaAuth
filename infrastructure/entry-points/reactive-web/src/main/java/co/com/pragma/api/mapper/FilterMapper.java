package co.com.pragma.api.mapper;

import co.com.pragma.api.constants.ApiConstants.ApiParams;
import co.com.pragma.model.user.filters.UserFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterMapper {
    public static UserFilter toFilter(MultiValueMap<String, String> params) {
        UserFilter.UserFilterBuilder builder = UserFilter.builder();

        getParam(params, ApiParams.ID_NUMBER_PARAM).ifPresent(builder::idNumber);
        getParam(params, ApiParams.EMAIL_PARAM).ifPresent(builder::email);
        getParam(params, ApiParams.NAME_PARAM).ifPresent(builder::name);
        getBigDecimalParam(params, ApiParams.MIN_BASE_SALARY_PARAM).ifPresent(builder::salaryGreaterThan);
        getBigDecimalParam(params, ApiParams.MAX_BASE_SALARY_PARAM).ifPresent(builder::salaryLowerThan);

        return builder
                .build();
    }

    private static Optional<String> getParam(MultiValueMap<String, String> params, String key) {
        return Optional.ofNullable(params.getFirst(key)).filter(s -> !s.isBlank());
    }

    private static Optional<BigDecimal> getBigDecimalParam(MultiValueMap<String, String> params, String key) {
        return getParam(params, key).flatMap(s -> {
            try {
                return Optional.of(new BigDecimal(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }
}
