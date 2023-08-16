package com.trade.bot.client;

import com.trade.bot.exception.ServiceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CbrClient {
    @Autowired
    private OkHttpClient client;

    @Value("${//TODO}")
    private String url;

    public String getXML() throws ServiceException {
        var request = new Request.Builder()
                .url(url)
                .build();

        try (var response = client.newCall(request).execute()){
            var body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new ServiceException("Че то данные не достаются ...", e);
        }
    }
}
