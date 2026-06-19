package com.sfeir.testbot;

import com.sfeir.testbot.telegram.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class TestBotApplicationTests {

    // TelegramBot's @PostConstruct registers with the Telegram API over the network
    // at startup, which would fail the context load in tests/CI. Replace it with a mock
    // so that registration never runs.
    @MockitoBean
    TelegramBot telegramBot;

    @Test
    void contextLoads() {
    }

    /**
     * The MCP client is disabled in tests (see src/test/resources/application.properties)
     * so the context can load without the external MCP servers running. That removes the
     * MCP-backed ToolCallbackProvider that AIAgent depends on, so we supply an empty one here.
     */
    @TestConfiguration
    static class TestToolCallbackConfig {
        @Bean
        ToolCallbackProvider testToolCallbackProvider() {
            return () -> new ToolCallback[0];
        }
    }

}
