package co.com.pragma.metrics.aws;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class MicrometerMetricPublisher implements MetricPublisher {
    private final ExecutorService service = Executors.newFixedThreadPool(10);
    private final MeterRegistry registry;

    @Override
    public void publish(MetricCollection metricCollection) {
        service.submit(() -> {
            List<Tag> tags = buildTags(metricCollection);
            metricCollection.stream()
                    .filter(recordMetric -> recordMetric.value() instanceof Duration || recordMetric.value() instanceof Integer)
                    .forEach(recordMetric -> {
                        if (recordMetric.value() instanceof Duration) {
                            registry.timer(recordMetric.metric().name(), tags).record((Duration) recordMetric.value());
                        } else if (recordMetric.value() instanceof Integer) {
                            registry.counter(recordMetric.metric().name(), tags).increment((Integer) recordMetric.value());
                        }
                    });
        });
    }

    @Override
    public void close() {
        //Not implemented
    }

    private List<Tag> buildTags(MetricCollection metricCollection) {
        return metricCollection.stream()
                .filter(recordMetric -> recordMetric.value() instanceof String || recordMetric.value() instanceof Boolean)
                .map(recordMetric -> Tag.of(recordMetric.metric().name(), recordMetric.value().toString()))
                .toList();
    }
}
