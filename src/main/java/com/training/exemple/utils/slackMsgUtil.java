package com.training.exemple.utils;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * slack webhook message send
 * 2022.10.07
 **/
@Slf4j
public class slackMsgUtil {

    private final static String webhookUrl = "https://hooks.slack.com/services/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";

    public static WebhookResponse msaSend(String msg) {
        WebhookResponse response = null;

        try {
            Slack slack = Slack.getInstance();
            Payload payload = Payload.builder().text(msg).build();
            response = slack.send(webhookUrl, payload);

            return response;
        } catch (Exception e) {
            log.error("slack msg send error : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
