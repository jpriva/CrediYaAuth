package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RoleDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class, RoleMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDomain_whenUserRequestDTOIsNull_shouldReturnNull() {
        User user = userMapper.toDomain(null);

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
                .role(roleDto)
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
        assertEquals(roleDto.getName(), user.getRole().getName());
    }

    @Test
    void toDomain_whenUserRequestDTOHasNullRole_shouldMapToUserWithNullRole() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .baseSalary(new BigDecimal("50000.00"))
                .role(null)
                .build();

        User user = userMapper.toDomain(userRequestDTO);

        assertNotNull(user);
        assertNull(user.getRole());
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
}