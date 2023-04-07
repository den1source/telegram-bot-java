package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread th = new Thread(
                () -> {
                    try {
                        start_bot();
                    } catch (TelegramApiException | SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        th.start();//запуск потока
        th.join();//ожидание его завершения (текущий поток засыпает до окончания th)

    }

    private static void start_bot() throws TelegramApiException, SQLException, ClassNotFoundException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
    }
}