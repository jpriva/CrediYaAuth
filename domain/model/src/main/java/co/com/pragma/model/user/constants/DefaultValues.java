package co.com.pragma.model.user.constants;

import java.math.BigDecimal;

public class DefaultValues {
    private DefaultValues(){}
    public static final String DEFAULT_ROLE = "CLIENTE";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final BigDecimal MAX_SALARY = new BigDecimal(15000000);
    public static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    public static final int MAX_LENGTH_NAME = 50;
    public static final int MAX_LENGTH_LAST_NAME = 50;
    public static final int MAX_LENGTH_EMAIL = 100;
    public static final int MAX_LENGTH_ID_NUMBER = 50;
    public static final int MAX_LENGTH_PHONE = 20;
    public static final int MAX_LENGTH_ADDRESS = 255;
}
