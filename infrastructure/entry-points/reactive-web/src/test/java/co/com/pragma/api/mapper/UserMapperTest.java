package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.FindUsersRequestDTO;
import co.com.pragma.api.dto.RoleDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class, RoleMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDomain_whenUserRequestDTOIsNull_shouldReturnNull() {
        User user = userMapper.toDomain((UserRequestDTO) null);

        assertNull(user);
    }

    @Test
    void toDomain_whenUserRequestDTOIsValid_shouldReturnUser() {
        RoleDTO roleDto = RoleDTO.builder().rolId(1).name("ADMIN").build();
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .baseSalary(new BigDecimal("50000.00"))
                .rolId(roleDto.getRolId())
                .phone("1234567890")
                .address("123 Main St")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        User user = userMapper.toDomain(userRequestDTO);

        assertNotNull(user);
        assertNull(user.getUserId(), "userId should be ignored and thus null");
        assertEquals(userRequestDTO.getName(), user.getName());
        assertEquals(userRequestDTO.getLastName(), user.getLastName());
        assertEquals(userRequestDTO.getEmail(), user.getEmail());
        assertEquals(userRequestDTO.getIdNumber(), user.getIdNumber());
        assertEquals(0, userRequestDTO.getBaseSalary().compareTo(user.getBaseSalary()));
        assertEquals(userRequestDTO.getPhone(), user.getPhone());
        assertEquals(userRequestDTO.getAddress(), user.getAddress());
        assertEquals(userRequestDTO.getBirthDate(), user.getBirthDate());

        assertNotNull(user.getRole());
        assertEquals(roleDto.getRolId(), user.getRole().getRolId());
    }

    @Test
    void toDomain_whenUserRequestDTOHasNullRole_shouldMapToUserWithEmptyRole() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .baseSalary(new BigDecimal("50000.00"))
                .rolId(null)
                .build();

        User user = userMapper.toDomain(userRequestDTO);

        assertNotNull(user);
        assertNotNull(user.getRole());
        assertNull(user.getRole().getDescription());
        assertNull(user.getRole().getName());
        assertNull(user.getRole().getRolId());
    }

    @Test
    void toResponseDto_whenUserIsNull_shouldReturnNull() {
        UserResponseDTO responseDTO = userMapper.toResponseDto(null);

        assertNull(responseDTO);
    }

    @Test
    void toResponseDto_whenUserIsValid_shouldReturnUserResponseDTO() {
        Role role = Role.builder().rolId(1).name("ADMIN").build();
        User user = User.builder()
                .userId(1)
                .name("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .idNumber("987654321")
                .baseSalary(new BigDecimal("60000.00"))
                .role(role)
                .phone("0987654321")
                .address("456 Oak Ave")
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        UserResponseDTO responseDTO = userMapper.toResponseDto(user);

        assertNotNull(responseDTO);
        assertEquals(user.getUserId(), responseDTO.getUserId());
        assertEquals(user.getName(), responseDTO.getName());
        assertEquals(user.getLastName(), responseDTO.getLastName());
        assertEquals(user.getEmail(), responseDTO.getEmail());
        assertEquals(user.getIdNumber(), responseDTO.getIdNumber());
        assertEquals(0, user.getBaseSalary().compareTo(responseDTO.getBaseSalary()));
        assertEquals(user.getPhone(), responseDTO.getPhone());
        assertEquals(user.getAddress(), responseDTO.getAddress());
        assertEquals(user.getBirthDate(), responseDTO.getBirthDate());

        assertNotNull(responseDTO.getRole());
        assertEquals(role.getRolId(), responseDTO.getRole().getRolId());
        assertEquals(role.getName(), responseDTO.getRole().getName());
    }

    @Test
    void toResponseDto_whenUserHasNullRole_shouldMapToUserResponseDTOWithNullRole() {
        User user = User.builder()
                .userId(1)
                .name("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .idNumber("987654321")
                .baseSalary(new BigDecimal("60000.00"))
                .role(null)
                .build();

        UserResponseDTO responseDTO = userMapper.toResponseDto(user);

        assertNotNull(responseDTO);
        assertNull(responseDTO.getRole());
    }

    @Test
    void toUserFilter_whenFindUserRequestDTONotNull_shouldMapToUserFilter(){
        FindUsersRequestDTO request = FindUsersRequestDTO.builder()
                .idNumber("123456789")
                .email("john.doe@example.com")
                .salaryLowerThan(new BigDecimal("50000"))
                .salaryGreaterThan(new BigDecimal("20000"))
                .name("John")
                .build();
        UserFilter userFilter = userMapper.toUserFilter(request);
        assertNotNull(userFilter);
        assertThat(userFilter.getIdNumber()).isEqualTo(request.getIdNumber());
        assertThat(userFilter.getEmail()).isEqualTo(request.getEmail());
        assertThat(userFilter.getSalaryGreaterThan()).isEqualTo(request.getSalaryGreaterThan());
        assertThat(userFilter.getSalaryLowerThan()).isEqualTo(request.getSalaryLowerThan());
        assertThat(userFilter.getName()).isEqualTo(request.getName());
    }

    @Test
    void toUserFilter_whenFindUserRequestDTONull_shouldBeNull(){
        UserFilter userFilter = userMapper.toUserFilter(null);
        assertThat(userFilter).isNull();
    }
}