package org.apereo.cas.support.events.publish.telegram;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import java.util.stream.Stream;

/**
 * Sample CAS events consuming Telegram Bot.
 *
 * @author Dmitriy Kopylenko
 */
public class CasEventsConsumingTelegramBot extends TelegramLongPollingBot {

    private String botToken;

    private String botUsername;

    private Set<Long> chatIds = new ConcurrentSkipListSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CasEventsConsumingTelegramBot.class);

    public CasEventsConsumingTelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOGGER.info("Received a chat bot update from Telegram servers: {}", update);
        this.chatIds.add(update.getMessage().getChatId());
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    public Stream<Long> getChatIdsCurrentlyConnectedToMe() {
        return this.chatIds.stream();
    }

    public void removeMeFromChat(Long chatId) {
        this.chatIds.remove(chatId);
    }
}
