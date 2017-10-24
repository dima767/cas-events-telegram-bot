package org.apereo.cas.support.events.publish.telegram;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CAS Telegram Bot configuration properties model.
 *
 * @author Dmitriy Kopylenko
 */
@ConfigurationProperties(prefix = "cas.telegram.bot")
public class CasTelegramBotConfigurationProperties {

    private String token;

    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
