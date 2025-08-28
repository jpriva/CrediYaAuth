package co.com.pragma.model.constants;

import java.math.BigDecimal;

public class DefaultValues {
    private DefaultValues(){}

    public static final String DEFAULT_ROLE_NAME = "CLIENTE";
    public static final int DEFAULT_ROLE_ID = 3;
    public static final String DEFAULT_ROLE_DESCRIPTION = "Cliente Solicitante";

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final BigDecimal MAX_SALARY = new BigDecimal(15000000);
    public static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    public static final int MAX_LENGTH_NAME = 50;
    public static final int MAX_LENGTH_LAST_NAME = 50;
    public static final int MAX_LENGTH_EMAIL = 100;
    public static final int MAX_LENGTH_ID_NUMBER = 50;
    public static final int MAX_LENGTH_PHONE = 20;
    public static final int MAX_LENGTH_ADDRESS = 255;

    public static final String NAME_FIELD = "Name";
    public static final String LAST_NAME_FIELD = "Last Name";
    public static final String EMAIL_FIELD = "Email";
    public static final String ID_NUMBER_FIELD = "Id Number";
    public static final String PHONE_FIELD = "Phone";
    public static final String ADDRESS_FIELD = "Address";
    public static final String SALARY_FIELD = "Salary";
    public static final String ROLE_FIELD = "Role";

}
