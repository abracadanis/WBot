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

    String cookies = "";

    String bodyTS = """
            {\"filter\":{\"appId\":252490,\"order\":0,\"minSales\":0,\"service1\":6,\"service2\":22,\"countMin1\":1," +
            "\"countMin2\":0,\"direction\":0,\"priceMax1\":0,\"priceMax2\":0,\"priceMin1\":0,\"priceMin2\":0,\"profitMax\":0," +
            "\"profitMin\":%s,\"priceType1\":0,\"priceType2\":0,\"salesPeriod\":0,\"salesService\":0,\"searchName\":\"\"," +
            "\"types\":{\"1\":1,\"2\":0,\"39\":0,\"40\":0,\"41\":0,\"42\":0}},\"fee1\":{\"fee\":10,\"bonus\":3}," +
            "\"fee2\":{\"fee\":9.91,\"bonus\":0},\"currency\":\"USD\"}
            """;

    String bodyST = """
            {\"filter\":{\"appId\":252490,\"order\":0,\"minSales\":0,\"service1\":22,\"service2\":6,\"countMin1\":1," +
            "\"countMin2\":0,\"direction\":0,\"priceMax1\":0,\"priceMax2\":0,\"priceMin1\":0,\"priceMin2\":0,\"profitMax\":0," +
            "\"profitMin\":%s,\"priceType1\":0,\"priceType2\":0,\"salesPeriod\":0,\"salesService\":0,\"searchName\":\"\"," +
            "\"types\":{\"1\":1,\"2\":0,\"39\":0,\"40\":0,\"41\":0,\"42\":0}},\"fee1\":{\"fee\":10,\"bonus\":3}," +
            "\"fee2\":{\"fee\":9.91,\"bonus\":0},\"currency\":\"USD\"}
            """;



    String minSt = "23";

    String minTs = "-12";

    public String getMinSt() {
        return minSt;
    }

    public String getMinTs() {
        return minTs;
    }

    @Override
    public List<Item> getItems(List<Item> newItems, Set<Long> ids) throws IOException, InterruptedException, JSONException {
        getNewItems(newItems, ids, bodyTS, minTs, "Tradeit", "SkinSwap");
        getNewItems(newItems, ids, bodyST, minSt, "SkinSwap", "Tradeit");

        return newItems;
    }

    public void getNewItems(List<Item> newItems, Set<Long> ids, String body, String min, String firstService, String secondService) throws IOException, InterruptedException, JSONException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.format(body, min)))
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
        for(Item i: items) {
            if(!ids.contains(i.getId())) {
                ids.add(i.getId());
                i.setFirstService(firstService);
                i.setSecondService(secondService);
                newItems.add(i);
            }
        }
    }

    public void setMinTs(String min) {
        minTs = min;
    }

    public void setMinSt(String min) {
        minSt = min;
    }

    public void setCookies(String userCookies) {
        cookies = userCookies;
    }


}
