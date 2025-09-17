package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserUseCaseTest {

    private UserUseCase useCase;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        useCase = new UserUseCase(userRepository);
    }

    @Test
    void validateReactive_shouldPass_whenValidUser() {
        User valid = new User(null, "Hugo", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(1000),
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        // Stub email validation for testing: always accept
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.validateReactive(valid))
                .expectSubscription()
                .expectNext(valid)    // ✅ emits the user
                .expectComplete()
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenNameBlank() {
        User invalid = new User(null, "", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(1000),
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.validateReactive(invalid))
                .expectSubscription()
                .expectErrorMessage("Validation failed: Name cannot be null or blank")
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenAmountNegative() {
        User invalid = new User(null, "Hugo", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(-5), LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.validateReactive(invalid))
                .expectSubscription()
                .expectErrorMessage("Validation failed: BaseSalary must be positive")
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenEmailAlreadyExists() {
        User user = new User(null, "Hugo", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(1000), LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        when(userRepository.existsByEmail("hugo@test.com"))
                .thenReturn(Mono.just(true)); // email already taken

        StepVerifier.create(useCase.validateReactive(user))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    assertEquals("Validation failed: Email already exists", error.getMessage());
                })
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenNameBlank_whenAmountNegative_whenEmailAlreadyExists() {

        User user = new User(null, " ", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(-5), LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        when(userRepository.existsByEmail("hugo@test.com"))
                .thenReturn(Mono.just(true)); // email already taken

        StepVerifier.create(useCase.validateReactive(user))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    assertEquals("Validation failed: Name cannot be null or blank, BaseSalary must be positive, Email already exists", error.getMessage());
                    assertEquals(3, ((ValidationException) error).getErrors().size());
                })
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenEmailFormatIsInvalid(){

        User invalid = new User(null, "Hugo", "Benjumea", "hugo@test",
                "3132142526", 123456L, 2, BigDecimal.valueOf(1000),
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        // Stub email validation for testing: always accept
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.validateReactive(invalid))
                .expectSubscription()
                .expectErrorMessage("Validation failed: The email format is invalid")
                .verify();
    }

    @Test
    void validateReactive_shouldFail_whenAmountIsNotInRange() {
        User invalid = new User(null, "Hugo", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(16000000), LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.validateReactive(invalid))
                .expectSubscription()
                .expectErrorMessage("Validation failed: BaseSalary must not be greater than 15000000")
                .verify();
    }

    @Test
    void saveUser_shouldPass_whenValidUser() {
        User valid = new User(null, "Hugo", "Benjumea", "hugo@test.com",
                "3132142526", 123456L, 2, BigDecimal.valueOf(1000),
                LocalDate.parse("1979-10-01"),
                "Calle falsa 123");

        // Stub email validation for testing: always accept
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        when(userRepository.saveUser(ArgumentMatchers.any())).thenReturn(Mono.just(valid));

        StepVerifier.create(useCase.saveUser(valid))
                .expectSubscription()
                .expectNext(valid)    // ✅ emits the user
                .expectComplete()
                .verify();
    }
}
