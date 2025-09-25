package co.com.pragma.sqs.sender;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.queue.gateways.SQSPort;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SQSSender implements SQSPort {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;
    private final LoggerPort logger;

    public Mono<String> send(String message) {
        return Mono.justOrEmpty(buildRequest(properties.queueUrl(), message))
                .doOnNext(sendMessage -> logger.info("Sending to sqs queue : [{}]", sendMessage.toString()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> logger.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String url, String message) {
        return SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> sendEmails(List<String> emails) {
        Map<String,Object> emailsMap = Map.of("emails",emails);
        return sendGenericMessage(emailsMap);
    }

    private <T> Mono<Void> sendGenericMessage(T payload) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            logger.debug("Sending email notification to SQS: {}", jsonMessage);
            return this.send(jsonMessage)
                    .doOnError(error -> logger.error("Failed to send payload for email notification. Error: {}", error.getMessage())).then();
        } catch (JsonProcessingException e) {
            logger.error("Error creating JSON payload for email notification. Error: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
