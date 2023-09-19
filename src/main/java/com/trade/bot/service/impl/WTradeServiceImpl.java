package com.trade.bot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.config.RandomUserAgent;
import com.trade.bot.service.WTradeService;
import com.trade.bot.service.obj.Item;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.*;

import org.json.*;

@Service
public class WTradeServiceImpl implements WTradeService {

    Map<Long, Float> id_price = new HashMap<>();

    List<Item> newItems = new ArrayList<>();

    HttpClient client = HttpClient.newHttpClient();

    String uri = "https://cs.money/5.0/load_bots_inventory/730?hasTradeLock=true&isMarket=true&limit=60&offset={0}&priceWithBonus=30&sort=botFirst&stickerCollection=EMS%20Katowice%202014&stickerCollection=DreamHack%202014&stickerCollection=ESL%20One%20Cologne%202014&stickerCollection=ESL%20One%20Katowice%202015&tradeLockDays=1&tradeLockDays=2&tradeLockDays=3&tradeLockDays=4&tradeLockDays=5&tradeLockDays=6&tradeLockDays=7&tradeLockDays=0&withStack=true";

    int offset = 0;

    @Override
    public List<Item> getItems() throws IOException, InterruptedException, JSONException {
        newItems.clear();
        getNewItems();

        return newItems;
    }

    public void getNewItems() throws IOException, InterruptedException, JSONException {
        List<Item> items;
        do {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))")
//                    .header("Cookie", "support_token=d20956a485adb7e78c92fe3af673a04d28dc5228fdee57556e3fd9097f7485fb")
                    .uri(URI.create(MessageFormat.format(uri, offset)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(request);
            System.out.println(response.body());

            JSONObject object = new JSONObject(response.body());
            JSONArray array = object.getJSONArray("items");

            ObjectMapper mapper = new ObjectMapper();
            items = mapper.readValue(array.toString(), new TypeReference<List<Item>>() {});
            for(Item i: items) {
                if(!id_price.containsKey(i.getId())) {
                    id_price.put(i.getId(), i.getPrice());
                    newItems.add(i);
                } else if(id_price.containsKey(i.getId()) && id_price.get(i.getId()) > i.getPrice()) {
                    id_price.replace(i.getId(), i.getPrice());
                    newItems.add(i);
                }
            }

            offset += 60;
            Thread.sleep(6000);
        } while (items.size() == 60);
        offset = 0;
        items.clear();
    }
}
