package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.usecase.user.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    //private final UserPath userPath;
    private final Handler userHandler;
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST("/api/v1/usuarios", userHandler::listenSaveUser)
                .onError(ValidationException.class, (ex, request) -> {
                    Map<String, Object> errorBody = Map.of(
                            "errors", ((ValidationException) ex).getErrors(),
                            "message", "Validation failed"
                    );

                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorBody);
                })
                .build();
    }
}
