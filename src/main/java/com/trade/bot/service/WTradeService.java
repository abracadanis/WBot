package com.trade.bot.service;

import javax.sql.rowset.serial.SerialException;

public interface WTradeService {
    String getPercents() throws SerialException;
}
