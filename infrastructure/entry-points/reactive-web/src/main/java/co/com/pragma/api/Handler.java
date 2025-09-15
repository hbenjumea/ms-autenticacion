package co.com.pragma.api;

import co.com.pragma.api.dto.SaveUserDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private  final UserUseCase userUseCase;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SaveUserDTO.class)
                .map(user -> objectMapper.convertValue(user, User.class))
                .flatMap(userUseCase::saveUser)
                .map(user -> objectMapper.convertValue(user, SaveUserDTO.class))
                .flatMap(savedUserDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUserDTO));       // <── log final ServerResponse
    }
}
