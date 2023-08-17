package com.trade.bot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.service.WTradeService;
import com.trade.bot.service.obj.Item;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.*;

@Service
public class WTradeServiceImpl implements WTradeService {

    String cookies = "theme=main; " +

            "session=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6IkpXVCJ9.eyJpZCI6Ijc2NTYxMTk4MTM1M" +
            "Dg3ODczIiwibmJmIjoxNjkyMjU5OTU5LCJleHAiOjE2OTQ4NTE5NTksImlhdCI6MTY5MjI1OTk1OX0.RMlLaP8ppTnq0FzZ7reX_NAVRi3So" +
            "4NaXdFrUVvUNpi6MRMjsulTRk_2Nxl_-xE7qPn0m2snzkpXWmWiV4bndJJeOUMpUc1CPm6a_4a0W3T5T0wXhD0ftw0FCEFwlqVUbW1Hvj__H" +
            "M52N6yA4rVgDHs95E2S1sOZ22Ytck8jGCmmK-Yjzmpo8FbphhnOlXPBixXrbSIZScHYv8BE1iXejCoxkMPxLyHd6dGSBEGzlHuf4ee_OR-u" +
            "AS2WEm8EKIE1PjZKAEv9fYinMv7EOo8PafIhl05RPWBKL8B8nT8Znr_kd2xyMv55cEpZujfi1tcV2awLM6X2fvIRrMqlrd_rOw; " +

            "steamid=76561198135087873";

    String bodyTS = "{\"filter\":{\"appId\":252490,\"order\":0,\"minSales\":0,\"service1\":6,\"service2\":22,\"countMin1\":1," +
            "\"countMin2\":0,\"direction\":0,\"priceMax1\":0,\"priceMax2\":0,\"priceMin1\":0,\"priceMin2\":0,\"profitMax\":0," +
            "\"profitMin\":-12,\"priceType1\":0,\"priceType2\":0,\"salesPeriod\":0,\"salesService\":0,\"searchName\":\"\"," +
            "\"types\":{\"1\":1,\"2\":0,\"39\":0,\"40\":0,\"41\":0,\"42\":0}},\"fee1\":{\"fee\":10,\"bonus\":3}," +
            "\"fee2\":{\"fee\":9.91,\"bonus\":0},\"currency\":\"USD\"}";

    String bodyST = "{\"filter\":{\"appId\":252490,\"order\":0,\"minSales\":0,\"service1\":22,\"service2\":6,\"countMin1\":1," +
            "\"countMin2\":0,\"direction\":0,\"priceMax1\":0,\"priceMax2\":0,\"priceMin1\":0,\"priceMin2\":0,\"profitMax\":0," +
            "\"profitMin\":23,\"priceType1\":0,\"priceType2\":0,\"salesPeriod\":0,\"salesService\":0,\"searchName\":\"\"," +
            "\"types\":{\"1\":1,\"2\":0,\"39\":0,\"40\":0,\"41\":0,\"42\":0}},\"fee1\":{\"fee\":10,\"bonus\":3}," +
            "\"fee2\":{\"fee\":9.91,\"bonus\":0},\"currency\":\"USD\"}";

    Set<Long> ids = new HashSet<>();

    @Override
    public List<Item> getItems() throws IOException, InterruptedException, JSONException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyTS))
                .header("Cookie", cookies)
                .header("Accept", "application/json, text/plain, */*")
                .header("Content-Type", "application/json")
                .uri(URI.create("https://tablevv.com/api/table/items-chunk?page=1"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        JSONObject object = new JSONObject(response.body());
        JSONArray array = object.getJSONArray("items");

        ObjectMapper mapper = new ObjectMapper();
        List<Item> items = mapper.readValue(array.toString(), new TypeReference<List<Item>>() {});
        List<Item> newItems = new ArrayList<>();
        for(Item i: items) {
            if(!ids.contains(i.getId())) {
                ids.add(i.getId());
                i.setFirstService("Tradeit");
                i.setSecondService("SkinSwap");
                newItems.add(i);
            }
        }



        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyST))
                .header("Cookie", cookies)
                .header("Accept", "application/json, text/plain, */*")
                .header("Content-Type", "application/json")
                .uri(URI.create("https://tablevv.com/api/table/items-chunk?page=1"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());


        object = new JSONObject(response.body());
        array = object.getJSONArray("items");

        items = mapper.readValue(array.toString(), new TypeReference<List<Item>>() {});
        for(Item i: items) {
            if(!ids.contains(i.getId())) {
                ids.add(i.getId());
                i.setFirstService("SkinSwap");
                i.setSecondService("Tradeit");
                newItems.add(i);
            }
        }

        return newItems;
    }

    public void setCookies(String userCookies) {
        cookies = userCookies;
    }


}
