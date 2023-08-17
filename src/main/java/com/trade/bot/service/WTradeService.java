package com.trade.bot.service;

import com.trade.bot.service.obj.Item;
import org.json.JSONException;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.List;

public interface WTradeService {
    List<Item> getItems() throws SerialException, IOException, InterruptedException, JSONException;

}
