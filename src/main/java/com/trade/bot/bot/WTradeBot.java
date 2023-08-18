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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class WTradeBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(WTradeBot.class);

    private static final String START = "/start";
    private static final String STOP = "/stop";
    private static final String PARSE = "/parse";
    private static final String CHANGE_MIN_TS = "/mints";
    private static final String CHANGE_MIN_ST = "/minst";
    private static final String COOKIES = "/cookies";
    private static final String INFO = "/info";


    private Boolean isStarted = false;
    private Boolean setMinSt = false;
    private Boolean setMinTs = false;
    private Boolean setCookies = false;
    private List<Item> newItems = new ArrayList<>();
    Set<Long> ids = new HashSet<>();

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
                sendMessage(chatId, "Погнали");
                LOG.debug("BOT START");

                isStarted = true;
                while(isStarted) {
                    try {
                        List<Item> items = service.getItems(newItems, ids);
                        for(Item i: items) {
                            String newItemMessage = """
                                NEW ITEM (%s -> %s): %s, %s
                                """;
                            sendMessage(chatId, String.format(newItemMessage, i.getFirstService(), i.getSecondService(), i.getName(), i.getProfit()));
                        }
                    } catch (IOException e) {
                        LOG.debug("ИОЭксепшн (что это ?) {} \n {}", e.getMessage(), e.getStackTrace());
                        sendMessage(chatId, "ИОЭксепшн (что это ?)");
                        isStarted = false;
                    } catch (InterruptedException e) {
                        LOG.debug("Ошибка при отправке запроса. {} \n {}", e.getMessage(), e.getStackTrace());
                        sendMessage(chatId, "Ошибка при отправке запроса");
                        isStarted = false;
                    } catch (JSONException e) {
                        LOG.debug("Ошибка при получении JSON. {} \n {}", e.getMessage(), e.getStackTrace());
                        sendMessage(chatId, "Ошибка при получении JSON");
                        isStarted = false;
                    }
                }
            }
            case STOP -> {
                isStarted = false;
                sendMessage(chatId, "Бот остановлен");
                LOG.debug("BOT STOP");
            }
            case COOKIES -> {
                LOG.debug("SET COOKIES");
                sendMessage(chatId, "Пиши");
                setCookies = true;
                setMinTs = false;
                setMinSt = false;
            }
            case CHANGE_MIN_ST -> {
                LOG.debug("BOT CHANGE MIN ST");
                if(isStarted) {
                    sendMessage(chatId, "Сначала останови бота (/stop)");
                }
                sendMessage(chatId, "Пиши");
                setMinSt = true;
                setMinTs = false;
            }
            case CHANGE_MIN_TS -> {
                LOG.debug("BOT CHANGE MIN TS");
                if(isStarted) {
                    sendMessage(chatId, "Сначала останови бота (/stop)");
                }
                sendMessage(chatId, "Пиши");
                setMinTs = true;
                setMinSt = false;
            }
            case INFO -> {
                LOG.debug("SEND INFO");
                sendInfo(chatId);
            }
            default -> {
                if(setMinTs) {
                    LOG.debug("MIN TS = {}", update.getMessage().getText());
                    service.setMinTs(update.getMessage().getText());
                } else if (setMinSt) {
                    LOG.debug("MIN ST = {}", update.getMessage().getText());
                    service.setMinSt(update.getMessage().getText());
                } else if(setCookies) {
                    service.setCookies(update.getMessage().getText());
                }
                setMinSt = false;
                setMinTs = false;
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
               Хрен его знает, на кой ляд тебе этот парсинг сдался, но я в чужие дела не лезу, хочешь спарсить, значит есть за чем...\n
               
               Запуск - /parse
               Остановка - /stop
               
               Изменение кукисов - /cookies
               Изменения минимума (Tradeit -> SkinSwap) - /mints
               Изменения минимума (SkinSwap -> Tradeit) - /minst
               Дефолтный минимум (Tradeit -> SkinSwap) = -12
               Дефолтный минимум (SkinSwap -> Tradeit) = 23

               Состояние бота и актуально заданные проценты - /info
               """;
        var formattedText = String.format(text, username);
        sendMessage(chatId, formattedText);
    }

    private void sendInfo(Long chatId) {
        String info;
        if (isStarted) {
            info = """
               Бот запущен
               Минимум (Tradeit -> SkinSwap) = %s
               Минимум (SkinSwap -> Tradeit) = %s
                
               Запуск - /parse
               Остановка - /stop
               
               Изменение кукисов - /cookies
               Изменения минимума (Tradeit -> SkinSwap) - /mints
               Изменения минимума (SkinSwap -> Tradeit) - /minst
                """;
        } else {
            info = """
               Бот остановлен
               Минимум (Tradeit -> SkinSwap) = %s
               Минимум (SkinSwap -> Tradeit) = %s
                
               Запуск - /parse
               Остановка - /stop
               
               Изменение кукисов - /cookies
               Изменения минимума (Tradeit -> SkinSwap) - /mints
               Изменения минимума (SkinSwap -> Tradeit) - /minst
                """;
        }

        sendMessage(chatId, String.format(info, service.getMinTs(), service.getMinSt()));
    }
}
