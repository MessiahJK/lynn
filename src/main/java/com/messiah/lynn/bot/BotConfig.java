package com.messiah.lynn.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jiangke
 * @date 2021/12/7
 */
@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
    private Long account;

    private String password;

    private Long masterAccount;
}
