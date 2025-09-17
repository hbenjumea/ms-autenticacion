package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository
> implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);

    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {

        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUser(User user) {

        return super.save(user)
                .doOnSuccess(u -> log.debug("Usuario almacenado: {}", u.getIdNumber()))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(String userEmail) {
        User user = User.builder()
                .email(userEmail)
                .build();
        return super.findByExample(user)
                .doOnNext(exists -> log.debug("existUserByEmail with param: {} -> {}", userEmail, exists))
                .next()
                .map(u -> true)
                .defaultIfEmpty(false);
    }


}
