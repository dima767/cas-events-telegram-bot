package org.apereo.cas.support.events.publish.telegram.config;

import org.apereo.cas.support.events.publish.telegram.CasEventsConsumingTelegramBot;
import org.apereo.cas.support.events.publish.telegram.CasEventsTelegramBotPublisher;
import org.apereo.cas.support.events.publish.telegram.CasTelegramBotConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring configuration class for cas events spring cloud stream subsystem.
 *
 * @author Dmitriy Kopylenko
 */
@Configuration
@EnableConfigurationProperties(CasTelegramBotConfigurationProperties.class)
public class CasEventsTelegramBotConfiguration {

    private final Logger logger = LoggerFactory.getLogger(CasEventsTelegramBotConfiguration.class);

    private List<BotSession> sessions = new ArrayList<>();

    static {
        ApiContextInitializer.init();
    }

    @Autowired
    private CasTelegramBotConfigurationProperties casTelegramBotProperties;

    @Bean
    public CasEventsConsumingTelegramBot casTelegramBot() {
        return new CasEventsConsumingTelegramBot(this.casTelegramBotProperties.getToken(), this.casTelegramBotProperties.getUsername());
    }

    @Bean
    public CasEventsTelegramBotPublisher casEventsTelegramBotPublisher(CasEventsConsumingTelegramBot casTelegramBot) {
        return new CasEventsTelegramBotPublisher(casTelegramBot);
    }

    @PostConstruct
    public void start() {
        CasEventsConsumingTelegramBot bot = casTelegramBot();
        logger.info("Starting config for telegram bot...");
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            logger.info("Registering polling bot: {}", bot);
            sessions.add(api.registerBot(bot));
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot {} due to error {}", bot.getBotUsername(), e.getMessage());
        }

    }

    @PreDestroy
    public void stop() {
        sessions.stream().forEach(session -> {
            if (session != null) {
                session.stop();
            }
        });
    }
}
