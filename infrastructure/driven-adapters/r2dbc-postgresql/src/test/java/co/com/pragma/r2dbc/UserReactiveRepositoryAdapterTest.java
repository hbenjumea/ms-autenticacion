package co.com.pragma.r2dbc;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UUID userId = UUID.fromString("17c13bcc-5d6d-4670-820b-7df2a0a880a4");
    private final UUID roleId = UUID.fromString("17c13bcc-5d6d-4670-820b-7df2a0a880a5");

    private final UserEntity userEntity = UserEntity.builder()
            .id(userId)
            .baseSalary(5000000.0)
            .email("hugo.benjumea2012@gmail.com")
            .name("Hugo Andres")
            .idNumber(72009606L)
            .idRole(roleId)
            .lastname("Benjumea Alvear")
            .phoneNumber("3132142555")
            .build();

    private final Role role = Role.builder()
            .id(roleId)
            .name("solicitante")
            .description("Usuario cliente")
            .build();
    private final User user = User.builder()
            .id(userId)
            .baseSalary(5000000.0)
            .email("hugo.benjumea2012@gmail.com")
            .name("Hugo Andres")
            .idNumber(72009606L)
            .role(role)
            .lastname("Benjumea Alvear")
            .phoneNumber("3132142555")
            .build();
/*
    @Test
    void mustFindValueById() {

        when(repository.findById("1")).thenReturn(Mono.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Mono<Object> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Flux<Object> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Flux<Object> result = repositoryAdapter.findByExample("test");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }
*/
    @Test
    void mustSaveValue() {

        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
