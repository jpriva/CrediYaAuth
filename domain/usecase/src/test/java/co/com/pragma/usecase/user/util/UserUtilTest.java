package co.com.pragma.usecase.user.util;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.exceptions.FieldSizeOutOfBoundsException;
import co.com.pragma.model.exceptions.FilterEmptyException;
import co.com.pragma.model.exceptions.SalaryUnboundException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.usecase.user.utils.UserUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Nested
    class VerifyUserBlankFieldsTests {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .baseSalary(new BigDecimal("50000.00"))
                .password("password123")
                .build();

        @ParameterizedTest
        @NullAndEmptySource
        void verifyUserNameBlankFields_shouldReturnError_whenAbsent(String field) {
            User userTest = user.toBuilder().name(field).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void verifyUserLastNameBlankFields_shouldReturnError_whenAbsent(String field) {
            User userTest = user.toBuilder().lastName(field).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void verifyUserEmailBlankFields_shouldReturnError_whenAbsent(String field) {
            User userTest = user.toBuilder().email(field).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void verifyUserIdNumberBlankFields_shouldReturnError_whenAbsent(String field) {
            User userTest = user.toBuilder().idNumber(field).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void verifyUserPasswordBlankFields_shouldReturnError_whenAbsent(String field) {
            User userTest = user.toBuilder().password(field).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void verifyUserBaseSalaryBlankFields_shouldReturnError_whenIsNull() {
            User userTest = user.toBuilder().baseSalary(null).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldBlankException.class)
                    .verify();
        }
    }

    @Nested
    class VerifyUserFieldSizeTests {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .baseSalary(new BigDecimal("50000.00"))
                .password("password123")
                .build();

        @Test
        void verifyUserNameFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().name("a".repeat(DefaultValues.MAX_LENGTH_NAME + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserLastNameFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().lastName("a".repeat(DefaultValues.MAX_LENGTH_LAST_NAME + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserEmailFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().email("a".repeat(DefaultValues.MAX_LENGTH_EMAIL + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserIdNumberFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().idNumber("a".repeat(DefaultValues.MAX_LENGTH_ID_NUMBER + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserAddressFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().address("a".repeat(DefaultValues.MAX_LENGTH_ADDRESS + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserPhoneFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().phone("a".repeat(DefaultValues.MAX_LENGTH_PHONE + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserAddressFieldSize_shouldReturnVoid_whenValidSize() {
            User userTest = user.toBuilder().address("a".repeat(DefaultValues.MAX_LENGTH_ADDRESS - 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest)).expectNext(userTest).verifyComplete();
        }

        @Test
        void verifyUserPhoneFieldSize_shouldReturnVoid_whenValidSize() {
            User userTest = user.toBuilder().phone("a".repeat(DefaultValues.MAX_LENGTH_PHONE - 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest)).expectNext(userTest).verifyComplete();
        }

        @Test
        void verifyUserPasswordFieldSize_shouldReturnError_whenToBig() {
            User userTest = user.toBuilder().password("a".repeat(DefaultValues.MAX_LENGTH_PASSWORD + 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();

            User userTest2 = user.toBuilder().password("a".repeat(DefaultValues.MIN_LENGTH_PASSWORD - 1)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest2))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void verifyUserBaseSalaryFieldSize_shouldReturnError_whenUnbounded() {
            User userTest = user.toBuilder().baseSalary(DefaultValues.MIN_SALARY.subtract(BigDecimal.ONE)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest))
                    .expectError(SalaryUnboundException.class)
                    .verify();
            User userTest2 = user.toBuilder().baseSalary(DefaultValues.MAX_SALARY.add(BigDecimal.ONE)).build();
            StepVerifier.create(UserUtils.verifyUserData(userTest2))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }
    }

    @Nested
    class AssignDefaultRoleTests {
        User userToAssignRole = User.builder()
                .name("John")
                .lastName("  Doe")
                .email("john.doe@example.com")
                .idNumber("12345")
                .phone("555-1234")
                .address("123 Main St")
                .baseSalary(new BigDecimal("50000.125"))
                .build();

        @Test
        void shouldAssignDefaultRole_whenMissing() {
            User user = UserUtils.assignDefaultRollIfMissing(userToAssignRole);
            assertThat(user.getRole()).isNotNull();
            assertThat(user.getRole().getName()).isNotNull();
            assertThat(user.getRole().getName()).isEqualTo(DefaultValues.DEFAULT_ROLE_NAME);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldAssignDefaultRole_whenNameIsMissing(String field) {
            User userRoleNameMissing = userToAssignRole.toBuilder().role(Role.builder().name(field).build()).build();
            User user = UserUtils.assignDefaultRollIfMissing(userRoleNameMissing);
            assertThat(user.getRole()).isNotNull();
            assertThat(user.getRole().getName()).isNotNull();
            assertThat(user.getRole().getName()).isEqualTo(DefaultValues.DEFAULT_ROLE_NAME);
        }

        @Test
        void shouldNotAssignDefaultRole_whenNameIsValued() {
            User userRoleNameMissing = userToAssignRole.toBuilder().role(Role.builder().name("aa").build()).build();
            User user = UserUtils.assignDefaultRollIfMissing(userRoleNameMissing);
            assertThat(user.getRole()).isNotNull();
            assertThat(user.getRole().getName()).isNotNull();
            assertThat(user.getRole().getName()).isNotEqualTo(DefaultValues.DEFAULT_ROLE_NAME);
        }

        @Test
        void shouldNotAssignDefaultRole_whenRolIdIsValued() {
            User userRoleNameMissing = userToAssignRole.toBuilder().role(Role.builder().rolId(1).build()).build();
            User user = UserUtils.assignDefaultRollIfMissing(userRoleNameMissing);
            assertThat(user.getRole()).isNotNull();
            assertThat(user.getRole().getRolId()).isNotNull();
            assertThat(user.getRole().getName()).isNotEqualTo(DefaultValues.DEFAULT_ROLE_NAME);
        }
    }

    @Nested
    class ValidateFilterTests{
        @Test
        void shouldReturnMono_whenHasOneProperty(){
            UserFilter filter1 = UserFilter.builder().salaryGreaterThan(new BigDecimal("50000")).build();
            StepVerifier.create(UserUtils.validateFilter(filter1))
                    .expectNext(filter1)
                    .verifyComplete();

            UserFilter filter2 = UserFilter.builder().salaryLowerThan(new BigDecimal("50000")).build();
            StepVerifier.create(UserUtils.validateFilter(filter2))
                    .expectNext(filter2)
                    .verifyComplete();

            UserFilter filter3 = UserFilter.builder().name("name").build();
            StepVerifier.create(UserUtils.validateFilter(filter3))
                    .expectNext(filter3)
                    .verifyComplete();

            UserFilter filter4 = UserFilter.builder().email("name@name.name").build();
            StepVerifier.create(UserUtils.validateFilter(filter4))
                    .expectNext(filter4)
                    .verifyComplete();

            UserFilter filter5 = UserFilter.builder().idNumber("123").build();
            StepVerifier.create(UserUtils.validateFilter(filter5))
                    .expectNext(filter5)
                    .verifyComplete();
        }
        @Test
        void shouldReturnComplete_whenNull(){
            StepVerifier.create(UserUtils.validateFilter(null)).expectComplete();
        }
        @Test
        void shouldReturnFilterEmptyException_whenEmpty(){
            UserFilter filter1 = UserFilter.builder().build();
            StepVerifier.create(UserUtils.validateFilter(filter1)).expectError(FilterEmptyException.class);
        }
    }
}
