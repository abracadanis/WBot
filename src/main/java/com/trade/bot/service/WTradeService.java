package com.trade.bot.service;

import com.trade.bot.service.obj.Item;
import org.json.JSONException;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface WTradeService {
    List<Item> getItems(List<Item> newItems, Set<Long> ids) throws SerialException, IOException, InterruptedException, JSONException;

}
