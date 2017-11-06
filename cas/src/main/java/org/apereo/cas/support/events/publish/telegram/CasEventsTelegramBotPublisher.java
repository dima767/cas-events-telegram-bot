package org.apereo.cas.support.events.publish.telegram;

import org.apereo.cas.support.events.ticket.CasTicketGrantingTicketCreatedEvent;
import org.apereo.cas.util.AsciiArtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Publish CAS events to Telegram via Telegram bit API
 *
 * @author Dmitriy Kopylenko
 */
public class CasEventsTelegramBotPublisher {

    private CasEventsConsumingTelegramBot casTelegramBot;

    private static final Logger LOGGER = LoggerFactory.getLogger(CasEventsTelegramBotPublisher.class);

    public CasEventsTelegramBotPublisher(CasEventsConsumingTelegramBot casTelegramBot) {
        this.casTelegramBot = casTelegramBot;
    }

    @EventListener
    public void handleCasTicketGrantingTicketCreatedEvent(final CasTicketGrantingTicketCreatedEvent event) {
        //Fancy log using ASCII art - just for fun ;-)
        AsciiArtUtils.printAsciiArtInfo(LOGGER, "SSO session established", formattedUserLoggedInMessage(event));

        //Broadcast Telegram message containing CAS event data to all chats currently connected to the bot
        casTelegramBot.getChatIdsCurrentlyConnectedToMe()
                .forEach(chatId -> {
                    try {
                        casTelegramBot.execute(new SendMessage(chatId, formattedUserLoggedInMessage(event)));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        casTelegramBot.removeMeFromChat(chatId);
                    }

                });
    }


    private static String formattedUserLoggedInMessage(CasTicketGrantingTicketCreatedEvent e) {
        return String.format("User [%s] logged in at [%s]",
                e.getTicketGrantingTicket().getAuthentication().getPrincipal().getId(),
                e.getTicketGrantingTicket().getCreationTime().toString());
    }
}
