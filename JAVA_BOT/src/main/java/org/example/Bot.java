package org.example;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Bot extends TelegramLongPollingBot {
    final int time_start_work = 8;
    final int time_stop_work = 20;
    int hour_for_next_day = 0;
    int h;
    int min;
    private ArrayList<String> array_emoji = new ArrayList<>();
    private ArrayList<String> all_products = new ArrayList<>();
    private ArrayList<String> all_types = new ArrayList<>();
    private long user_id;
    private String product_from_cart, addres_for_booking;
    public boolean number_entry = false, in_cart = false, in_my_orders = false, choose_hour_for_order = false, choose_min_for_order = false;
    public ArrayList<String> selected_products = new ArrayList<>();
    public ArrayList<Integer> selected_products_quantity = new ArrayList<>();
    public ArrayList<Double> selected_products_price = new ArrayList<>();
    public ArrayList<Integer> selected_products_time = new ArrayList<>();
    ///
    Map map = new Map();
    ///

    private void filling() throws SQLException, ClassNotFoundException {
        tovar_BD tovar_bd = new tovar_BD();
        if (array_emoji.size() == 0) {
            int kol_vida = tovar_bd.get_all_type_of_products().size();
            for (int i = 1; i <= kol_vida; i++) {
                String sum = "";
                try (BufferedReader br = new BufferedReader(new FileReader("D:\\Learning\\Git\\JAVA_BOT\\data_EM\\" + i + ".txt"))) {
                    String s;
                    while ((s = br.readLine()) != null) {
                        sum += (s);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                array_emoji.add(EmojiParser.parseToUnicode(sum));

            }
        }
        if (all_types.size() == 0) {
            all_types = tovar_bd.get_all_type_of_products();
        }
        if (all_products.size() == 0) {
            all_products = tovar_bd.get_all_products();
        }
        System.out.println(array_emoji);
        System.out.println(all_types);
    }

    @Override
    public String getBotUsername() {
        String name = "";
        try (BufferedReader br = new BufferedReader(new FileReader("name_bot.txt"))) {
            int c;
            while ((c = br.read()) != -1) {
                name += Character.toString(c);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return name;
    }

    @Override
    public String getBotToken() {
        String key = "";
        try (BufferedReader br = new BufferedReader(new FileReader("key.txt"))) {
            int c;

            while ((c = br.read()) != -1) {
                key += Character.toString(c);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return key;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            filling();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ///
        add_user_id add_user_id = null;
        try {
            add_user_id = new add_user_id();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                Message message_from_user = update.getMessage();
                String chatId = message_from_user.getChatId().toString();
                user_id = Math.toIntExact(message_from_user.getFrom().getId());
                try {
                    add_user_id.add_data_in_table_of_users(user_id);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    button(message_from_user.getText(), chatId, user_id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Message message_from_bot = update.getCallbackQuery().getMessage();
            String chatId = message_from_bot.getChatId().toString();
            String Butt_Mess = update.getCallbackQuery().getData();
            try {
                add_user_id.add_data_in_table_of_users(user_id);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                subbutton(Butt_Mess, chatId, user_id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        new Thread(() -> {
            map.get_image_of_map("ул. Зорге, 42А/127, микрорайон Западный", user_id);
        }).start();

        //System.out.println("id user: " + user_id);
        System.out.println("Выбранный товар: " + selected_products);
        System.out.println("Количество товара: " + selected_products_quantity);
        System.out.println("Цена выбранного на число: " + selected_products_price);

    }

    public void button(String mess, String chatId, long user_id) throws SQLException, ClassNotFoundException, TelegramApiException {
        //in_cart = false;
        tovar_BD tovar_bd = new tovar_BD();
        ArrayList<String> vid = tovar_bd.get_all_type_of_products();
        if (!isNumeric(mess)) {
            check_last_product_for_find_errors(chatId);
            if (!vid.contains(mess)) {
                switch (mess) {
                    case "/start", "Начать занаво (это очистит вашу карзину)" -> {
                        String text = "Добро пожаловать в \"Пекарня\" бот\uD83E\uDD16! Мы очень рады\uD83D\uDE04, что вы здесь. Хотите ли вы намазанный маслом круассан\uD83E\uDD50,  вкусный торт\uD83C\uDF82 или печенье с шоколадной крошкой\uD83C\uDF6A, у нас вы найдете\uD83D\uDD0E все это. Благодаря нашему боту\uD83E\uDD16 вы можете подобрать идеальную✅ для вас выпечку\uD83C\uDF5E!\n";
                        hello(chatId, user_id, text);
                        clear_all();
                        in_my_orders = false;
                        in_cart=false;
                    }

                    case "Ассортимент" -> {
                        String text = "Выберите изделия которые хотите заказать";
                        choose_product_type(chatId, user_id, text);
                        in_my_orders = false;
                        in_cart=false;
                    }
                    case "⬅Назад" -> {
                        String text = "Что мы можем сделать для вас?";
                        hello(chatId, user_id, text);
                        in_my_orders = false;
                        in_cart=false;
                    }
                    case "Настройки" -> {
                        String text = "Подробнее";
                        setting(chatId, text);
                        in_my_orders = false;
                        in_cart=false;
                    }
                    case "О \uD83E\uDD16" -> {
                        String text = "Этот бот был создан студентами 3 курса ЮФУ с целью научиться создавать бота, работать с базами данных, и улучшить свои навыки программирования на java.";
                        some_text(chatId, text);
                    }
                    case "\uD83E\uDDFA Корзина" -> {//корзина
                        hello(chatId,user_id,"\uD83E\uDDFA Корзина:");
                        cart_of_user(chatId, "Корзина пуста");
                        in_my_orders = false;

                    }
                    case "Мои заказы" -> {
                        data_DB data_db = new data_DB();
                        add_user_id add_user_id = new add_user_id();
                        if (data_db.all_id_order_for_user(add_user_id.ID_USER_TO_PR_KEY(user_id)).size() > 0) {
                            button_for_cancel_booking(chatId);
                        } else {
                            String text = "У вас нет заказов";
                            some_text(chatId, text);
                        }
                    }
                }
            } else {

                String text = "Выберите товар:";
                choose_product(chatId, user_id, text, mess);
            }
        } else {
            if (!choose_min_for_order) {
                if (!choose_hour_for_order) {
                    if (in_cart) {
                        change_quantity_product(chatId, mess);
                    } else {
                        if (number_entry) {
                            entering_number_for_item(mess, chatId);
                        }
                    }
                } else {
                    h = parseInt(mess);
                    choose_hour_for_order = false;
                    choose_time_min(chatId);
                }
            } else {
                choose_min_for_order = false;
                min = parseInt(mess);
                if (hour_for_next_day != 0) {
                    if(min==1 || min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9) some_text(chatId, "Ваше время на следущий день:\n" + h + ":0" + min);
                    else some_text(chatId, "Ваше время на следущий день:\n" + h + ":" + min);
                } else {
                    if(min==1 || min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9) some_text(chatId, "Ваше время:\n" + h + ":0" + min);
                    else some_text(chatId, "Ваше время:\n" + h + ":" + min);
                }
                String time;
                if(min==1 || min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9) time= h + ":0" + min;
                else time= h + ":" + min;
                make_order(chatId, addres_for_booking, user_id, time);
            }
        }

    }

    public static boolean isNumeric(String str) {
        try {
            parseInt(str);
            return parseInt(str) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void change_quantity_product(String chatId, String mess) throws SQLException, ClassNotFoundException, TelegramApiException {
        if (selected_products.size() == selected_products_price.size() && selected_products.size() == selected_products_quantity.size() && selected_products.size() != 0) {
            int index = selected_products.indexOf(product_from_cart);
            selected_products_quantity.set(index, Integer.valueOf(mess));
            tovar_BD tovar_bd = new tovar_BD();
            double kol_vo = Double.parseDouble(mess);
            selected_products_price.set(index, tovar_bd.price_of_product(product_from_cart) * kol_vo);
            cart_of_user(chatId, "");
        }
    }

    public void subbutton(String mess, String chatId, long user_id) throws SQLException, ClassNotFoundException, TelegramApiException {
        if (!in_my_orders) {
            read_shops read_shops = new read_shops();
            if (!read_shops.get_all_shops().contains(mess)) {
                if (!in_cart) {//|| !mess.contains("❌") !mess.contains("\uD83E\uDDFA")
                    if (isNumeric(mess)) {
                        if (number_entry) {
                            entering_number_for_item(mess, chatId);
                        }
                    } else {
                        if (selected_products.size() == selected_products_price.size() && all_products.contains(mess)) {
                            selected_products.add(mess);
                            message_of_quantity(chatId);
                        } else if (all_products.contains(mess)) {
                            check_last_product_for_find_errors(chatId);
                            selected_products.add(mess);
                            message_of_quantity(chatId);
                            System.out.println("Несанкционипрванннн");
                        }
                    }
                } else {
                    if (!isNumeric(mess)) {
                        if (mess.contains("❌") && in_cart) {
                            String product_from_cart = mess.replace("❌", "");
                            int index = selected_products.indexOf(product_from_cart);
                            ///
                            selected_products.remove(index);
                            selected_products_price.remove(index);
                            selected_products_quantity.remove(index);
                            String text = product_from_cart + " был удлен";
                            some_text(chatId, text);
                            cart_of_user(chatId, "");
                        } else if (mess.contains("\uD83E\uDDFA") &&in_cart) {
                            product_from_cart = mess.replace("\uD83E\uDDFA", "");
                            change_quantity_in_cart(chatId);
                        } else if (mess.equals("Очистить корзину") &&in_cart) {
                            clear_all();
                            in_cart = false;
                            //some_text(chatId, "");
                            String text = "Корзина очищена! Что мы можем сделать для вас еще?";
                            hello(chatId, user_id, text);

                        } else if (mess.equals("Забронировать")) {
                            send_map(chatId);
                            in_cart = false;
                        }
                    } else {
                        change_quantity_product(chatId, mess);
                    }
                }
            } else {

                addres_for_booking = mess;
                some_text(chatId, "Вы выбрали пекарню по адресу: <u><b> " + mess + "</b></u>");
                tovar_BD tovar_bd = new tovar_BD();
                selected_products_time.clear();
                for (String s : selected_products) {
                    selected_products_time.add(tovar_bd.read_time(s));
                }
                {
                    int max = selected_products_time.get(0);
                    for (int i = 1; i < selected_products_time.size(); i++) {
                        if (selected_products_time.get(i) > max) {
                            max = selected_products_time.get(i);
                        }
                    }
                    in_my_orders = false;
                    choose_time_hour(chatId, max);
                }


            }
        } else {
            data_DB data_db = new data_DB();
            add_user_id add_user_id = new add_user_id();
            tovar_BD tovar_bd = new tovar_BD();
            read_shops read_shops = new read_shops();
            data_db.get_data_user_id(add_user_id.ID_USER_TO_PR_KEY(user_id), tovar_bd.get_all_products(), tovar_bd.read_all_id(), read_shops.get_all_shops());
            if (mess.contains("+")) {
                Long tt = Long.valueOf((mess.substring(0, mess.indexOf("+"))));
                if (data_db.id_order_bd_1.contains(tt)) {
                    if (mess.contains("✅")) {
                        String path_QR = new QRCodeGenerator().qr_code(String.valueOf(tt), tt);
                        try {
                            SendPhoto sendPhoto = new SendPhoto();
                            sendPhoto.setChatId(chatId);
                            sendPhoto.setPhoto(new InputFile(new File(path_QR)));
                            execute(sendPhoto);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (mess.contains("❌")) {
                        data_db.delete_order(tt);
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText("Заказ №" + tt + " успешно удалён");
                        execute(message);
                    }
                } else {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Вы уже забрали этот товар");
                    execute(message);
                }
            } else {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Вы нажали на кнопку из предыдущего сообщения!");
                execute(message);
            }

        }

    }


    private void button_for_cancel_booking(String chatId) throws SQLException, ClassNotFoundException, TelegramApiException {
        in_my_orders = true;

        HashMap<Long, Integer> map = new HashMap<Long, Integer>();
        ArrayList<Long> numera_zakazov;
        int size;

        data_DB data_db = new data_DB();
        add_user_id add_user_id = new add_user_id();
        tovar_BD tovar_bd = new tovar_BD();
        read_shops read_shops = new read_shops();

        data_db.get_data_user_id(add_user_id.ID_USER_TO_PR_KEY(user_id), tovar_bd.get_all_products(), tovar_bd.read_all_id(), read_shops.get_all_shops());

        ArrayList<Long> id_order_bd = data_db.id_order_bd_1;
        ArrayList<String> type_xleba = data_db.type_tovar_1;
        ArrayList<Integer> kol_vo = data_db.quantity_1;
        ArrayList<Double> sum = data_db.sum_1;
        ArrayList<String> adresa_for_vuvod = data_db.all_shops;
        ArrayList<String> time_for_vuvod = data_db.time_1;
        {
            for (Long word : id_order_bd) {
                map.put(word, Collections.frequency(id_order_bd, word));
            }
            size = map.size();
            numera_zakazov = new ArrayList<>(map.keySet());
        }

        for (int i = 0; i < size; i++) {
            int finalI = i;
            new Thread(() -> {
                Double summm = 0.0;
                String tt = "";

                SendMessage message = new SendMessage();
                tt += "Заказ №" + numera_zakazov.get(finalI) + "\n====================\n";
                for (int j = 0; j < type_xleba.size(); j++) {
                    if (id_order_bd.get(j).equals(numera_zakazov.get(finalI))) {
                        message.setChatId(chatId);
                        message.enableHtml(true);
                        String vid = null;
                        try {
                            vid = tovar_bd.get_type_of_product(type_xleba.get(j));
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        tt += array_emoji.get(all_types.indexOf(vid)) + " " + type_xleba.get(j) + " - " + kol_vo.get(j) + " шт. - " + sum.get(j) + " руб\n";
                        summm += sum.get(j);
                    }
                }
                tt += "====================\nПо адресу:<u> " + adresa_for_vuvod.get(finalI) + "</u>\nВремя: <b><u>" + time_for_vuvod.get(finalI) + "</u></b>" + "\nИтого: " + summm + " рублей";
                InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> list_of_list_of_buttons = new ArrayList<>();
                message.setText(tt);
                List<InlineKeyboardButton> buttons_list = new ArrayList<>();
                {
                    var button = new InlineKeyboardButton();
                    button.setText("❌ Отмена заказа");
                    button.setCallbackData(numera_zakazov.get(finalI).toString() + "+❌");
                    buttons_list.add(button);
                }
                {
                    var button = new InlineKeyboardButton();
                    button.setText("✅ QR-код");
                    button.setCallbackData(numera_zakazov.get(finalI).toString() + "+✅");
                    buttons_list.add(button);
                }
                list_of_list_of_buttons.add(buttons_list);

                markupKeyboard.setKeyboard(list_of_list_of_buttons);
                message.setReplyMarkup(markupKeyboard);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }


    }

    private void check_last_product_for_find_errors(String chatId) throws TelegramApiException, SQLException, ClassNotFoundException {
        if (selected_products.size() != selected_products_quantity.size() && number_entry) {
            tovar_BD tovar_bd = new tovar_BD();
            String tovar_ = selected_products.get(selected_products.size() - 1);
            String text = "Вы не ввели кол-во для:\n=========================\n<u>" + array_emoji.get(all_types.indexOf(tovar_bd.get_type_of_product(tovar_))) + " " + tovar_ + "</u>\n=========================\nПоэтому товар будет удален из корзины";
            selected_products.remove(selected_products.size() - 1);
            some_text(chatId, text);
        }
    }

    private void send_map(String chatId) throws TelegramApiException, SQLException, ClassNotFoundException {
        {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId);
            photo.setPhoto(new InputFile(new File("D:\\Learning\\Git\\JAVA_BOT\\data_MAP\\" + user_id + ".png")));
            execute(photo);
        }
        {
            read_shops read_shops = new read_shops();
            ArrayList<String> arrayList = read_shops.get_all_shops();
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Нажимите на кнопку, чтобы выбрать адрес доставки");
            InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            for (String s : arrayList) {
                List<InlineKeyboardButton> button1 = new ArrayList<>();
                var number = new InlineKeyboardButton();
                number.setText(s);
                number.setCallbackData(s);
                button1.add(number);
                buttons.add(button1);
            }
            markupKeyboard.setKeyboard(buttons);
            message.setReplyMarkup(markupKeyboard);
            execute(message);
        }
    }

    private void clear_all() {
        selected_products.clear();
        selected_products_quantity.clear();
        selected_products_price.clear();
        selected_products_time.clear();
    }

    private void some_text(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(text);
        execute(message);
    }

    private void choose_time_hour(String chatId, int max_time) throws TelegramApiException {
        choose_hour_for_order = true;
        String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        String[] array = timeStamp.split(":");
        int time = (Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1])) + 5 + max_time;//текущее время+время приготовления в минутах
        h = time / 60;
        min = time % 60;
        max_time = max_time / 60;
        if (h >= time_stop_work) {
            String text = "Теперь нужно выбрать удобное для Вас время. Начнем с часов. Когда вам удобно? (Имейте ввиду что доступное время расчитывается исходя из минимального времени готовки блюд). <u>ВНИМАНИЕ! Сейчас уже поздно, поэтому заказ будет перенесен на следущий день</u>";
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.enableHtml(true);
            message.setText(text);

            ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
            Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
            Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
            for (int i = time_start_work + max_time; i < time_stop_work; i++) {
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton(String.valueOf(i)));
                keyboardRows.add(keyboardRow);
            }
            hour_for_next_day = 1;
            Keyboard_menu.setKeyboard(keyboardRows);
            message.setReplyMarkup(Keyboard_menu);
            execute(message);
        } else {
            String text = "Теперь нужно выбрать удобное для Вас время. Начнем с часов. Когда вам удобно? (Имейте ввиду что доступное время расчитывается исходя из минимального времени готовки блюд)";
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);

            ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
            Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
            Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();

            for (int i = h; i < time_stop_work; i++) {
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton(String.valueOf(i)));
                keyboardRows.add(keyboardRow);
            }

            Keyboard_menu.setKeyboard(keyboardRows);
            message.setReplyMarkup(Keyboard_menu);

            execute(message);
        }

    }

    private void choose_time_min(String chatId) throws TelegramApiException {
        choose_min_for_order = true;
        String text = "Теперь минуты. Когда вам удобно?";
        if (hour_for_next_day != 0) {
            text += " <u>ВНИМАНИЕ! Сейчас уже поздно, поэтому заказ будет перенесен на следущий день</u>";
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(text);

        ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
        Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
        Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        for (int i = min; i < 60; i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(String.valueOf(i)));
            keyboardRows.add(keyboardRow);
        }

        Keyboard_menu.setKeyboard(keyboardRows);
        message.setReplyMarkup(Keyboard_menu);
        execute(message);

    }

    private void entering_number_for_item(String mess, String chatId) throws SQLException, ClassNotFoundException, TelegramApiException {
        tovar_BD tovar_bd = new tovar_BD();

        if (!in_cart) {
            selected_products_quantity.add(Integer.valueOf(mess));
            selected_products_price.add(tovar_bd.price_of_product(selected_products.get(selected_products.size() - 1)) * selected_products_quantity.get(selected_products_quantity.size() - 1));
            number_entry = false;
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            String xleb = selected_products.get(selected_products.size() - 1);
            String vid = tovar_bd.get_type_of_product(xleb);
            message.setText("В вашу корзину добавлен:\n" + array_emoji.get(all_types.indexOf(vid)) + " " + xleb + "\n" + selected_products_quantity.get(selected_products_quantity.size() - 1) + " шт.\nна сумму " + selected_products_price.get(selected_products_price.size() - 1) + "₽");
            execute(message);
        } else {
            number_entry = false;
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            String xleb = selected_products.get(selected_products.indexOf(product_from_cart));
            String vid = tovar_bd.get_type_of_product(xleb);
            message.setText("В вашу корзину добавлен:\n" + array_emoji.get(all_types.indexOf(vid)) + " " + xleb + "\n" + selected_products_quantity.get(selected_products.indexOf(product_from_cart)) + " шт.\nна сумму " + selected_products_price.get(selected_products.indexOf(product_from_cart)) + "₽");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            execute(message);
            cart_of_user(chatId, "");
        }


    }


    private void setting(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
        Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
        Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow_2 = new KeyboardRow();
        KeyboardRow keyboardRow_3 = new KeyboardRow();
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow_2);
        keyboardRows.add(keyboardRow_3);
        keyboardRow.add(new KeyboardButton("О \uD83E\uDD16"));
        keyboardRow_2.add(new KeyboardButton("Начать занаво (это очистит вашу карзину)"));
        keyboardRow_3.add(new KeyboardButton("⬅Назад"));
        Keyboard_menu.setKeyboard(keyboardRows);
        message.setReplyMarkup(Keyboard_menu);
        execute(message);
    }

    private void hello(String chatId, long user_id, String text) throws TelegramApiException, SQLException, ClassNotFoundException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
        Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
        Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow_2 = new KeyboardRow();
        KeyboardRow keyboardRow_3 = new KeyboardRow();
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow_2);
        keyboardRows.add(keyboardRow_3);
        data_DB data_db = new data_DB();
        //keyboardRow.add(new KeyboardButton("Ассортимент"));
        keyboardRow_2.add(new KeyboardButton("\uD83E\uDDFA Корзина"));
        keyboardRow_2.add(new KeyboardButton("Ассортимент"));
        add_user_id add_user_id = new add_user_id();
        keyboardRow_3.add(new KeyboardButton("Настройки"));
        Keyboard_menu.setKeyboard(keyboardRows);
        message.setReplyMarkup(Keyboard_menu);
        if (data_db.all_id_order_for_user(add_user_id.ID_USER_TO_PR_KEY(user_id)).size() > 0) {
            keyboardRow_3.add(new KeyboardButton("Мои заказы"));
        }
        execute(message);
    }

    private void choose_product_type(String chatId, long user_id, String text) throws TelegramApiException, SQLException, ClassNotFoundException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        ReplyKeyboardMarkup Keyboard_menu = new ReplyKeyboardMarkup();
        Keyboard_menu.setResizeKeyboard(true); //подгоняем размер
        Keyboard_menu.setOneTimeKeyboard(false); //скрываем после использования
        tovar_BD tovar_bd = new tovar_BD();
        ArrayList<String> arrayList = tovar_bd.get_all_type_of_products();
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; arrayList.size() - 1 > i; i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            if (!arr.contains(arrayList.get(i))) {
                keyboardRow.add(new KeyboardButton(arrayList.get(i)));
                if (i == arrayList.size() - 2) keyboardRow.add(new KeyboardButton(arrayList.get(i + 1)));
                keyboardRows.add(keyboardRow);
                arr.add(arrayList.get(i));
                if (i == arrayList.size() - 2) arr.add(arrayList.get(i + 1));
            }
        }

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("\uD83E\uDDFA Корзина"));
        keyboardRow.add(new KeyboardButton("⬅Назад"));
        keyboardRows.add(keyboardRow);

        Keyboard_menu.setKeyboard(keyboardRows);
        message.setReplyMarkup(Keyboard_menu);
        execute(message);


    }


    private void choose_product(String chatId, long user_id, String text, String mes_from_user) throws SQLException, ClassNotFoundException, TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        tovar_BD tovar_bd = new tovar_BD();
        ArrayList<String> name_TOVARA = tovar_bd.all_products_of_type(mes_from_user);
        ArrayList<Double> price_TOVATA = tovar_bd.all_price_of_type(mes_from_user);
        List<List<InlineKeyboardButton>> list_of_list_of_buttons = new ArrayList<>();
        message.setText(text);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();

        for (int i = 0; i < name_TOVARA.size(); i++) {
            String xleb = name_TOVARA.get(i);
            Double price = price_TOVATA.get(i);
            if (!selected_products.contains(xleb)) {
                List<InlineKeyboardButton> buttons_list = new ArrayList<>();
                var button = new InlineKeyboardButton();
                button.setText(xleb + " " + price + " ₽");
                button.setCallbackData(xleb);
                buttons_list.add(button);
                list_of_list_of_buttons.add(buttons_list);
            }

        }
        if (list_of_list_of_buttons.size() == 0) {
            message.setText("Больше нечего выбирать. Если хотите еще что-то заказать забронируйте текущий заказ.\nЗатем нажмите /start и выберите Ассортимент");
            execute(message);
        } else {
            markupKeyboard.setKeyboard(list_of_list_of_buttons);
            message.setReplyMarkup(markupKeyboard);
            execute(message);
        }
    }

    private void message_of_quantity(String chatId) throws TelegramApiException, SQLException, ClassNotFoundException {
        String tovar_ = selected_products.get(selected_products.size() - 1);
        {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(new File("D:\\Learning\\Git\\JAVA_BOT\\data_PHOTO_TOVARA\\" + tovar_ + ".jpg")));
            execute(sendPhoto);
        }
        number_entry = true;
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        tovar_BD tovar_bd = new tovar_BD();
        message.setText("Выберите или введите с клавиатуры количество которое вам необходимо для:\n" + "<b><u>" + array_emoji.get(all_types.indexOf(tovar_bd.get_type_of_product(tovar_))) + " " + tovar_ + "</u></b>");
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        buttons.add(button1);

        {
            for (int j = 1; j <= 3; j++) {
                var number = new InlineKeyboardButton();
                number.setText(String.valueOf(j));
                number.setCallbackData(String.valueOf(j));
                button1.add(number);
            }
        }
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    private void change_quantity_in_cart(String chatId) throws TelegramApiException, SQLException, ClassNotFoundException {
        number_entry = true;
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        tovar_BD tovar_bd = new tovar_BD();
        message.setText("Выберите или введите с клавиатуры количество которое вам необходимо для:\n" + "<b><u>" + array_emoji.get(all_types.indexOf(tovar_bd.get_type_of_product(product_from_cart))) + " " + product_from_cart + "</u></b>");
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        buttons.add(button1);

        {
            for (int j = 1; j <= 3; j++) {
                var number = new InlineKeyboardButton();
                number.setText(String.valueOf(j));
                number.setCallbackData(String.valueOf(j));
                button1.add(number);
            }
        }
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    private void cart_of_user(String chatId, String text) throws TelegramApiException, SQLException, ClassNotFoundException {
        in_cart = true;
        if (selected_products.size() == 0) {
            some_text(chatId, text);
        } else {
            Double sum = 0.0;
            //data.vibrann_xleb_summ.clear();
            {
                for (int i = 0; i < selected_products_price.size(); i++) {
                    sum += selected_products_price.get(i);
                }
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Если хотите изменить кол-во - нажмите на товар и введите либо выберите новое кол-во");
            InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> list_of_list_of_buttons = new ArrayList<>();

            for (int i = 0; i < selected_products.size(); i++) {
                tovar_BD tovar_bd = new tovar_BD();
                String xleb = selected_products.get(i);
                String vid = tovar_bd.get_type_of_product(xleb);

                List<InlineKeyboardButton> buttons_list = new ArrayList<>();
                var button = new InlineKeyboardButton();

                button.setText(array_emoji.get(all_types.indexOf(vid)) + " " + xleb);
                button.setCallbackData(xleb + "\uD83E\uDDFA");

                buttons_list.add(button);

                var but = new InlineKeyboardButton();
                but.setText(selected_products_quantity.get(i) + " шт." + " " + selected_products_price.get(i) + " ₽" + " ❌");
                but.setCallbackData(xleb + "❌");
                buttons_list.add(but);

                list_of_list_of_buttons.add(buttons_list);
            }
            {
                List<InlineKeyboardButton> buttons_list = new ArrayList<>();
                var button = new InlineKeyboardButton();
                button.setText("Очистить корзину");
                button.setCallbackData("Очистить корзину");
                buttons_list.add(button);
                list_of_list_of_buttons.add(buttons_list);
            }
            {
                List<InlineKeyboardButton> buttons_list = new ArrayList<>();
                var button = new InlineKeyboardButton();
                button.setText("Забронировать на сумму\n" + sum + " ₽");
                button.setCallbackData("Забронировать");
                buttons_list.add(button);
                list_of_list_of_buttons.add(buttons_list);
            }


            markupKeyboard.setKeyboard(list_of_list_of_buttons);
            message.setReplyMarkup(markupKeyboard);
            execute(message);
        }

    }

    public static String generate_key_for_order(int len) {
        // Диапазон ASCII – буквенно-цифровой (0-9, a-z, A-Z)
        //final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        // каждая итерация цикла случайным образом выбирает символ из заданного
        // диапазон ASCII и добавляет его к экземпляру `StringBuilder`
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    private String filling_map_and_send_to_data_base(String adres, long user_id, String time) throws SQLException, ClassNotFoundException {
        String uniq_key;
        data_DB data_db = new data_DB();
        ///
        {
            metka:
            {
                uniq_key = generate_key_for_order(10);
                if (data_db.All_id_order_bd().contains(uniq_key)) {
                    break metka;
                }
            }
        }
        ///
        tovar_BD tovar_bd = new tovar_BD();
        ArrayList<Integer> id = tovar_bd.read_all_id();
        ArrayList<String> tovar = tovar_bd.get_all_products();
        ///
        ArrayList<Integer> vibrann_xleb_id = new ArrayList<>();
        ///
        for (String s : selected_products) {
            vibrann_xleb_id.add(id.get(tovar.indexOf(s)));
        }

        read_shops r_s = new read_shops();

        ArrayList<HashMap> list_of_map = new ArrayList<>();
        add_user_id add_user_id = new add_user_id();
        for (int i = 0; i < selected_products.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id_order", uniq_key);
            map.put("id_user", String.valueOf(add_user_id.ID_USER_TO_PR_KEY(user_id)));//map.put("id_user", String.valueOf(data.user_id));
            map.put("adres", String.valueOf(r_s.get_all_shops().indexOf(adres)));
            map.put("type", String.valueOf(vibrann_xleb_id.get(i)));
            map.put("kol_vo", String.valueOf(selected_products_quantity.get(i)));
            map.put("sum", String.valueOf(selected_products_price.get(i)));
            map.put("time", time);
            list_of_map.add(map);
        }
        data_db.add_data(list_of_map);
        return uniq_key;
    }

    private void make_order(String chatId, String adres, long user_id, String time) throws TelegramApiException, SQLException, ClassNotFoundException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText("Заказ №" + filling_map_and_send_to_data_base(adres, user_id, time) + " по адресу <b><u>" + adres + "</u></b>, в примерное время " + time + " выполенен, ожидайте!\n<u>ВНИМАНИЕ!</u>\nОплата происходит только <u><b>при получении товара</b></u>.");
        execute(message);
        hello(chatId, user_id, "Что мы можем для Вас сделать еще?");
        clear_all();
        h = 0;
        min = 0;
        product_from_cart = null;
        addres_for_booking = null;

    }

}
