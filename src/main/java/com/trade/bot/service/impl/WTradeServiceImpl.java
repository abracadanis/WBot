package com.trade.bot.service.impl;

import com.trade.bot.client.CbrClient;
import com.trade.bot.service.WTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialException;

@Service
public class WTradeServiceImpl implements WTradeService {

    @Autowired
    private CbrClient client;

    @Override
    public String getPercents() throws SerialException {
        return null;
    }


}
