package co.com.pragma.logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Slf4jLoggerAdapterTest {

    @Mock
    private Logger mockLogger;

    @Test
    @DisplayName("should delegate debug call to the underlying SLF4J logger")
    void debug_shouldDelegateCall() {
        try (MockedStatic<LoggerFactory> mockedFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockedFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
            Slf4jLoggerAdapter loggerAdapter = new Slf4jLoggerAdapter(this.getClass());
            String message = "Debug message: {}";
            Object[] arguments = {"test-debug"};

            loggerAdapter.debug(message, arguments);

            verify(mockLogger, times(1)).debug(message, arguments);
        }
    }

    @Test
    @DisplayName("should delegate info call to the underlying SLF4J logger")
    void info_shouldDelegateCall() {
        try (MockedStatic<LoggerFactory> mockedFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockedFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
            Slf4jLoggerAdapter loggerAdapter = new Slf4jLoggerAdapter(this.getClass());
            String message = "Info message: {}";
            Object[] arguments = {"test-info"};
            loggerAdapter.info(message, arguments);

            verify(mockLogger, times(1)).info(message, arguments);
        }
    }

    @Test
    @DisplayName("should delegate warn call to the underlying SLF4J logger")
    void warn_shouldDelegateCall() {
        try (MockedStatic<LoggerFactory> mockedFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockedFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
            Slf4jLoggerAdapter loggerAdapter = new Slf4jLoggerAdapter(this.getClass());
            String message = "Warn message: {}";
            Object[] arguments = {"test-warn"};

            loggerAdapter.warn(message, arguments);

            verify(mockLogger, times(1)).warn(message, arguments);
        }
    }

    @Test
    @DisplayName("should delegate error call to the underlying SLF4J logger")
    void error_shouldDelegateCall() {
        try (MockedStatic<LoggerFactory> mockedFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockedFactory.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
            Slf4jLoggerAdapter loggerAdapter = new Slf4jLoggerAdapter(this.getClass());
            String message = "Error message: {}";
            Object[] arguments = {new RuntimeException("Test exception")};

            loggerAdapter.error(message, arguments);

            verify(mockLogger, times(1)).error(message, arguments);
        }
    }
}