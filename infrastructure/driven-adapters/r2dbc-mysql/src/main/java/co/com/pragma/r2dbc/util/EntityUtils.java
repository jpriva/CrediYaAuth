package co.com.pragma.r2dbc.util;

import lombok.NoArgsConstructor;

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

}
