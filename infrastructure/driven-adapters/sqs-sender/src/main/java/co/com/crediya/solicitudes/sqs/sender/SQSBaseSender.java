package co.com.crediya.solicitudes.sqs.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public abstract class SQSBaseSender {
    protected final SqsAsyncClient client;

    protected Mono<String> send(String message, String queueUrl) {
        return Mono.fromCallable(() -> buildRequest(message, queueUrl, null, null))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent to queue {}: {}", queueUrl, response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    protected Mono<String> send(String message, String queueUrl, String messageGroupId, String messageDeduplicationId) {
        return Mono.fromCallable(() -> buildRequest(message, queueUrl, messageGroupId, messageDeduplicationId))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent to queue {}: {}", queueUrl, response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String queueUrl, String messageGroupId, String messageDeduplicationId) {
        SendMessageRequest.Builder builder = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message);

        if (queueUrl != null && queueUrl.endsWith(".fifo")) {
            String dedupId = messageDeduplicationId != null
                    ? messageDeduplicationId
                    : UUID.nameUUIDFromBytes(message.getBytes(StandardCharsets.UTF_8)).toString();
            String groupId = messageGroupId != null ? messageGroupId : "solicitudes";
            builder = builder
                    .messageGroupId(groupId)
                    .messageDeduplicationId(dedupId);
        }

        return builder.build();
    }
}
