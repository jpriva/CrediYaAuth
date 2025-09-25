package co.com.pragma.model.queue.gateways;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SQSPort {

    Mono<Void> sendEmails(List<String> emails);
}
