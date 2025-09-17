package co.com.pragma.api;

import co.com.pragma.api.dto.SaveUserDTO;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, UserUseCase.class, UserRepository.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserUseCase userUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnValidationErrors_whenInvalidUser() {
        // given: an invalid user request
        SaveUserDTO invalidUser = new SaveUserDTO(
                "",                // invalid name
                "Smith",
                "john@example.com",
                123456L,
                2,
                new BigDecimal("-1000"), // invalid salary
                "3132142526",
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123"
        );

        // mock repository: email not exists
        Mockito.when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(Mono.just(false));

        // when: sending the request
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()

                // then: validate response
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation failed")
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[?(@ == 'Name cannot be null or blank')]").exists()
                .jsonPath("$.errors[?(@ == 'BaseSalary must be positive')]").exists();
    }

    @Test
    void shouldReturnEmailExistsError_whenEmailAlreadyRegistered() {
        // given
        SaveUserDTO user = new SaveUserDTO("John", "Smith", "john@example.com", 123456L,
                2,
                new BigDecimal("2000"), // invalid salary
                "3132142526",
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        // mock repository: email already exists
        Mockito.when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(Mono.just(true));

        // when
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()

                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[?(@ == 'Email already exists')]").exists();
    }

    @Test
    public void shouldSaveUser_whenValidInput(){

        // given: valid input
        SaveUserDTO userDTO = new SaveUserDTO("John", "Smith", "john@example.com", 123456L,
                2,
                new BigDecimal("2000"), // invalid salary
                "3132142526",
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        User user = objectMapper.convertValue(userDTO, User.class);

        when(userUseCase.saveUser(ArgumentMatchers.any())).thenReturn(Mono.just(user));

        Mockito.when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(Mono.just(false));

        // when & then
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("John")
                .jsonPath("$.lastname").isEqualTo("Smith")
                .jsonPath("$.email").isEqualTo("john@example.com")
                .jsonPath("$.baseSalary").isEqualTo(2000);
    }
}
