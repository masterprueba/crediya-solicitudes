package co.com.crediya.solicitudes.consumer;

import co.com.crediya.solicitudes.consumer.dto.ValidateTokenRequest;
import co.com.crediya.solicitudes.consumer.dto.ValidateTokenResponse;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthValidationRestAdapterTest {


}