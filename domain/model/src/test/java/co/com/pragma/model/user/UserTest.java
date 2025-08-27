package co.com.pragma.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void trimFields_shouldTrimAllStringFieldsAndScaleBaseSalary() {
        User user = User.builder()
                .name("  John  ")
                .lastName("  Doe  ")
                .email("  john.doe@example.com  ")
                .idNumber("  12345  ")
                .phone("  555-1234  ")
                .address("  123 Main St  ")
                .baseSalary(new BigDecimal("12345.6789"))
                .build();

        user.trimFields();

        assertEquals("John", user.getName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("12345", user.getIdNumber());
        assertEquals("555-1234", user.getPhone());
        assertEquals("123 Main St", user.getAddress());
        assertEquals(new BigDecimal("12345.68"), user.getBaseSalary());
    }

    @Test
    void trimFields_shouldHandleNullFieldsGracefully() {
        User user = User.builder().build();

        user.trimFields();

        assertNull(user.getName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getIdNumber());
        assertNull(user.getPhone());
        assertNull(user.getAddress());
        assertNull(user.getBaseSalary());
    }

    @Test
    void trimFields_shouldHandleEmptyFields() {
        User user = User.builder()
                .name("")
                .lastName("")
                .email("")
                .idNumber("")
                .phone("")
                .address("")
                .baseSalary(new BigDecimal("1000.00"))
                .build();

        user.trimFields();

        assertEquals("", user.getName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getIdNumber());
        assertEquals("", user.getPhone());
        assertEquals("", user.getAddress());
        assertEquals(new BigDecimal("1000.00"), user.getBaseSalary());
    }

}
