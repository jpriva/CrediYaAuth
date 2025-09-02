package co.com.pragma.password;

import co.com.pragma.password.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BCryptPasswordEncoderAdapter.class, SecurityConfig.class})
class BCryptPasswordEncoderAdapterTest {

    @Autowired
    private BCryptPasswordEncoderAdapter passwordEncoderAdapter;

    @Test
    void encode_shouldReturnHashedPassword() {
        String rawPassword = "my-plain-text-password";

        String result = passwordEncoderAdapter.encode(rawPassword);

        assertThat(result)
                .isNotNull()
                .isNotEqualTo(rawPassword)
                .startsWith("$2a$16$");//Bcrypt prefix
    }

    @Test
    void matches_shouldReturnTrueForMatch() {
        String rawPassword = "my-plain-text-password";
        String encodedPassword = passwordEncoderAdapter.encode(rawPassword);

        boolean result = passwordEncoderAdapter.matches(rawPassword, encodedPassword);

        assertThat(result).isTrue();
    }

    @Test
    void matches_shouldReturnFalseForMismatch() {
        String rawPassword = "my-plain-text-password";
        String wrongPassword = "wrong-password";
        String encodedPassword = passwordEncoderAdapter.encode(rawPassword);

        boolean result = passwordEncoderAdapter.matches(wrongPassword, encodedPassword);

        assertThat(result).isFalse();
    }
}