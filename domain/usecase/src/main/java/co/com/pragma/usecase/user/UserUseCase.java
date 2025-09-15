package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return validateReactive(user)
                .flatMap(userRepository::saveUser);
    }

    protected Mono<User> validateReactive(User user) {

        List<String> syncErrors = Stream.of(
                validate(isNotBlank(user.getName()), "Name cannot be null or blank"),
                validate(isNotBlank(user.getLastname()), "Lastname cannot be null or blank"),
                validate(isNotBlank(user.getEmail()), "Email cannot be null or blank"),
                validate(isEmailValid(user.getEmail()), "The email format is invalid"),
                validate(isPositive(user.getBaseSalary()), "BaseSalary must be positive"),
                validate(isInRange(user.getBaseSalary()), "BaseSalary must not be greater than 15000000")
        ).flatMap(Optional::stream).toList();

        Mono<Optional<String>> emailError = userRepository.existsByEmail(user.getEmail())
                .map(exists -> exists
                        ? Optional.of("Email already exists")
                        : Optional.empty());

        return emailError.flatMap(emailErr -> {
            List<String> allErrors = Stream.concat(
                    syncErrors.stream(),
                    emailErr.stream()
            ).toList();

            return allErrors.isEmpty()
                    ? Mono.just(user)
                    : Mono.error(new ValidationException(allErrors));
        });
    }

    private Optional<String> validate(boolean condition, String message) {
        return condition ? Optional.empty() : Optional.of(message);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isInRange(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.valueOf(15000001)) < 0;
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
