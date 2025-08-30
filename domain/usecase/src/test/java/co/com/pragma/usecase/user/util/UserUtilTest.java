package co.com.pragma.usecase.user.util;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.utils.UserUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserUtilTest {

    @Test
    void trim_shouldTrimAllStringFieldsAndScaleSalary() {
        User userToTrim = User.builder()
                .name("  John  ")
                .lastName("  Doe  ")
                .email("  john.doe@example.com  ")
                .idNumber("  12345  ")
                .phone("  555-1234  ")
                .address("  123 Main St  ")
                .baseSalary(new BigDecimal("50000.125"))
                .build();

        Mono<User> trimmedUserMono = UserUtils.trim(userToTrim);

        StepVerifier.create(trimmedUserMono)
                .assertNext(trimmedUser -> {
                    assertEquals("John", trimmedUser.getName());
                    assertEquals("Doe", trimmedUser.getLastName());
                    assertEquals("john.doe@example.com", trimmedUser.getEmail());
                    assertEquals("12345", trimmedUser.getIdNumber());
                    assertEquals("555-1234", trimmedUser.getPhone());
                    assertEquals("123 Main St", trimmedUser.getAddress());
                    assertEquals(new BigDecimal("50000.13"), trimmedUser.getBaseSalary());
                })
                .verifyComplete();
    }

    @Test
    void trim_shouldHandleNullFields() {
        User userWithNulls = User.builder()
                .name(null)
                .lastName("  Doe  ")
                .email(null)
                .idNumber("  12345  ")
                .phone(null)
                .address(null)
                .baseSalary(null)
                .build();

        Mono<User> trimmedUserMono = UserUtils.trim(userWithNulls);

        StepVerifier.create(trimmedUserMono)
                .assertNext(trimmedUser -> {
                    assertNull(trimmedUser.getName());
                    assertEquals("Doe", trimmedUser.getLastName());
                    assertNull(trimmedUser.getEmail());
                    assertEquals("12345", trimmedUser.getIdNumber());
                    assertNull(trimmedUser.getPhone());
                    assertNull(trimmedUser.getAddress());
                    assertNull(trimmedUser.getBaseSalary());
                })
                .verifyComplete();
    }

    @Test
    void trim_shouldHandleAllNullUser() {
        User allNullUser = User.builder().build();

        Mono<User> trimmedUserMono = UserUtils.trim(allNullUser);

        StepVerifier.create(trimmedUserMono)
                .assertNext(trimmedUser -> {
                    assertNull(trimmedUser.getName());
                    assertNull(trimmedUser.getLastName());
                    assertNull(trimmedUser.getEmail());
                    assertNull(trimmedUser.getIdNumber());
                    assertNull(trimmedUser.getPhone());
                    assertNull(trimmedUser.getAddress());
                    assertNull(trimmedUser.getBaseSalary());
                })
                .verifyComplete();
    }

    @Test
    void trim_shouldHandleEmptyAndWhitespaceOnlyStrings() {
        User userWithEmptyStrings = User.builder()
                .name("")
                .lastName("   ")
                .email("  test@test.com  ")
                .build();

        Mono<User> trimmedUserMono = UserUtils.trim(userWithEmptyStrings);

        StepVerifier.create(trimmedUserMono)
                .assertNext(trimmedUser -> {
                    assertEquals("", trimmedUser.getName());
                    assertEquals("", trimmedUser.getLastName());
                    assertEquals("test@test.com", trimmedUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void trim_shouldScaleSalaryCorrectly_roundingDown() {
        User user = User.builder()
                .baseSalary(new BigDecimal("98765.4321"))
                .build();

        Mono<User> trimmedUserMono = UserUtils.trim(user);

        StepVerifier.create(trimmedUserMono)
                .assertNext(trimmedUser -> {
                    assertEquals(new BigDecimal("98765.43"), trimmedUser.getBaseSalary());
                })
                .verifyComplete();
    }

    @Test
    void trim_shouldNotChangeAlreadyTrimmedAndScaledUser() {
        User cleanUser = User.builder()
                .name("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .idNumber("67890")
                .phone("555-5678")
                .address("456 Oak Ave")
                .baseSalary(new BigDecimal("75000.50"))
                .build();

        Mono<User> processedUserMono = UserUtils.trim(cleanUser);

        StepVerifier.create(processedUserMono)
                .assertNext(trimmedUser -> {
                    assertEquals(cleanUser.getName(), trimmedUser.getName());
                    assertEquals(cleanUser.getLastName(), trimmedUser.getLastName());
                    assertEquals(cleanUser.getEmail(), trimmedUser.getEmail());
                    assertEquals(cleanUser.getIdNumber(), trimmedUser.getIdNumber());
                    assertEquals(cleanUser.getPhone(), trimmedUser.getPhone());
                    assertEquals(cleanUser.getAddress(), trimmedUser.getAddress());
                    assertEquals(cleanUser.getBaseSalary(), trimmedUser.getBaseSalary());
                }).verifyComplete();
    }
}
