package com.trade.bot.bot;

import com.trade.bot.service.impl.WTradeServiceImpl;
import com.trade.bot.service.obj.Item;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;


@Component
public class WTradeBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(WTradeBot.class);

    private static final String START = "/start";
    private static final String STOP = "/stop";
    private static final String PARSE = "/parse";
    private static final String CHANGE_MIN_TS = "/mints";
    private static final String CHANGE_MIN_ST = "/minst";


    private Boolean isStarted = false;
    private Boolean isChangingMinSt = false;
    private Boolean isChangingMinTs = false;

    @Autowired
    private WTradeServiceImpl service;


    public WTradeBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch(message) {
            case START -> {
                String username = update.getMessage().getChat().getUserName();
                startCommand(chatId, username);
            }
            case PARSE -> {
                isStarted = true;
                while(isStarted) {
                    try {
                        List<Item> items = service.getItems();
                        for(Item i: items) {
                            String newItemMessage = """
                                NEW ITEM (%s -> %s): %s, %s
                                """;
                            sendMessage(chatId, String.format(newItemMessage, i.getFirstService(), i.getSecondService(), i.getName(), i.getProfit()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case STOP -> {
                isStarted = false;
            }
            case CHANGE_MIN_ST -> {
                if(isStarted) {
                    sendMessage(chatId, "Сначала останови бота (/stop)");
                }
                isChangingMinSt = true;
                isChangingMinTs = false;
            }
            case CHANGE_MIN_TS -> {
                if(isStarted) {
                    sendMessage(chatId, "Сначала останови бота (/stop)");
                }
                isChangingMinTs = true;
                isChangingMinSt = false;
            }
            default -> {
                if(isChangingMinTs) {
                    service.setMinTs(update.getMessage().getText());
                } else if (isChangingMinSt) {
                    service.setMinSt(update.getMessage().getText());
                }
                isChangingMinSt = false;
                isChangingMinTs = false;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "vvTrable_bot";
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.debug("Ошибка отправки сообщения. Error Message: {}", e.getMessage());
        }
    }

    private void startCommand(Long chatId, String username) {
        var text = """
               Короче, %s, я тебе парсер сделал и в благородство играть не буду: выполнишь для меня пару заданий — и мы в расчете.
               Заодно посмотрим, как быстро у тебя башка после плотной хапочки прояснится. А по твоей теме постараюсь разузнать.
               Хрен его знает, на кой ляд тебе этот парсинг сдался, но я в чужие дела не лезу, хочешь спарсить, значит есть за чем...
               
               Запуск - /parse
               Остановка - /stop
               Изменения минимума (Tradeit -> SkinSwap) - /mints
               Изменения минимума (SkinSwap -> Tradeit) - /minst
               """;
        var formattedText = String.format(text, username);
        sendMessage(chatId, formattedText);
    }
}
