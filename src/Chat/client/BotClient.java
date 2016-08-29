package Chat.client;


import Chat.ConsoleHelper;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client
{
    private static int botsCounter = 0;

    public static void main(String args []) {
        new BotClient().run();
    }
    public class BotSocketThread extends SocketThread{
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            //	С помощью метода sendTextMessage() отправь сообщение с текстом
            sendTextMessage("Привет. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");

            // Вызови реализацию clientMainLoop() родительского класса
            super.clientMainLoop();

        }


        @Override
        protected void processIncomingMessage(String message) {

            // Вывести в консоль текст полученного сообщения message
            ConsoleHelper.writeMessage(message);

            // Получить из message имя отправителя и текст сообщения. Они разделены ": "
            String senderName = "";
            String senderMessageText;

            if (message.contains(": ")) {
                senderName = message.substring(0, message.indexOf(": "));
                senderMessageText = message.substring(message.indexOf(": ") + 2);
            }
            else {
                senderMessageText = message;
            }


            SimpleDateFormat format = null;
            // Отправить ответ в зависимости от текста принятого сообщения. Если текст сообщения:
            if ("дата".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("d.MM.YYYY");
            }
            else if ("день".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("d");
            }
            else if ("месяц".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("MMMM");
            }
            else if ("год".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("YYYY");
            }
            else if ("время".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("H:mm:ss");
            }
            else if ("час".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("H");
            }
            else if ("минуты".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("m");
            }
            else if ("секунды".equalsIgnoreCase(senderMessageText)) {
                format = new SimpleDateFormat("s");
            }

            if (format != null)
            {
                sendTextMessage("Информация для " + senderName + ": " + format.format(Calendar.getInstance().getTime()));
            }

        }

    }

    @Override
    protected SocketThread getSocketThread() {

        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole() {

        return false;
    }

    @Override
    protected String getUserName() {
        if (botsCounter == 99) {
            botsCounter = 0;
        }
        return "date_bot_" + botsCounter++;

    }
}