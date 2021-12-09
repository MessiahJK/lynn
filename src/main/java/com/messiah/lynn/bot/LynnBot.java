package com.messiah.lynn.bot;

import com.sun.tools.javac.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiangke
 * @date 2021/12/6
 */
@Slf4j
@Component
public class LynnBot {



    @Autowired
    private BotConfig botConfig;

    private final String defaultDeviceInfoFilePath = "device.json";

    private final List<Long> groupList = List.of(667487900L);

    private final Map<String,Integer> map = new ConcurrentHashMap<>(16);

    @PostConstruct
    public void init() {
        Bot bot = BotFactory.INSTANCE.newBot(botConfig.getAccount(), botConfig.getPassword(), new BotConfiguration() {{
            fileBasedDeviceInfo(defaultDeviceInfoFilePath); // 使用 device.json 存储设备信息
            setProtocol(MiraiProtocol.ANDROID_PHONE); // 切换协议
        }});
        bot.login();
        afterLogin(bot);
        groupMessageTest(bot);
    }

    private void afterLogin(Bot bot) {
        bot.getEventChannel().subscribeAlways(FriendMessageEvent.class, (event) -> {
            if (event.getSender().getId() == botConfig.getMasterAccount()) {
                System.out.println("event:" + event.getMessage());
                for (SingleMessage singleMessage : event.getMessage()) {
                    if (singleMessage instanceof Image) {
                        System.out.println("image");
                        System.out.println(((Image) singleMessage).isEmoji());

                    } else if (singleMessage instanceof MarketFace) {
                        System.out.println("market face");
                    } else if (singleMessage instanceof PlainText) {
                        System.out.println("text");
                    }
                    System.out.println("singleMessage:" + singleMessage.contentToString());
                }
                System.out.println("event1:");
                event.getSubject().sendMessage(new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append("Hi, you just said: '")
                        .append(event.getMessage())
                        .append("'")
                        .build()
                );

            }
        });
    }

    private void groupMessageTest(Bot bot) {
        bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
            if(groupList.contains(event.getGroup().getId())){
                for (SingleMessage singleMessage : event.getMessage()) {
                    if (singleMessage instanceof Image && !((Image) singleMessage).isEmoji()) {
                        String key = event.getGroup().getId()+"-"+((Image) singleMessage).getImageId();
                        if(map.containsKey(key)){
                            var sum = map.get(key);
                            sum++;
                            map.put(key,sum);
                            event.getSubject().sendMessage(new MessageChainBuilder()
                                    .append(new QuoteReply(event.getMessage()))
                                    .append("此图片已经被群友发了")
                                    .append(String.valueOf(sum))
                                    .append("次了哦～")
                                    .build()
                            );
                        }else{
                            map.put(key,1);
                        }
                    }
                }
            }
        });
    }
}
