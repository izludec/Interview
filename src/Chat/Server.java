package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    private static class Handler extends Thread
    {
         private Socket socket;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException,ClassNotFoundException {

            while (true) {
                // Сформировать и отправить команду запроса имени пользователя
                connection.send(new Message(MessageType.NAME_REQUEST));
                // Получить ответ клиента
                Message message = connection.receive();
                String user = message.getData();
                // Проверить, что получена команда с именем пользователя
                if (message.getType().equals(MessageType.USER_NAME)) {

                    //Достать из ответа имя, проверить, что оно не пустое
                    if (user != null && !user.isEmpty()) {

                        // и пользователь с таким именем еще не подключен (используй connectionMap)
                        if (!connectionMap.containsKey(user)) {

                            // Добавить нового пользователя и соединение с ним в connectionMap
                            connectionMap.put(user, connection);
                            // Отправить клиенту команду информирующую, что его имя принято
                            connection.send(new Message(MessageType.NAME_ACCEPTED));

                            // Вернуть принятое имя в качестве возвращаемого значения
                            return user;
                        }
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {

            for (String key : connectionMap.keySet()) {
                Message message = new Message(MessageType.USER_ADDED, key);

                if (!key.equals(userName)) {
                    connection.send(message);
                }
            }
        }


        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {

            while (true) {

                Message message = connection.receive();
                // Если принятое сообщение – это текст (тип TEXT)
                if (message.getType() == MessageType.TEXT) {

                    String s = userName + ": " + message.getData();

                    Message formattedMessage = new Message(MessageType.TEXT, s);
                    sendBroadcastMessage(formattedMessage);
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }

        @Override
        public void run()
        {
            ConsoleHelper.writeMessage("Установлено соединение с сервером"+socket.getRemoteSocketAddress());
            String clientName = null;
            try (Connection connection = new Connection(socket)) {
                clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED,clientName));
                sendListOfUsers(connection,clientName);
                serverMainLoop(connection,clientName);

            }
         catch (IOException e) {
        ConsoleHelper.writeMessage("Ошибка при обмене данными с удаленным адресом");
             } catch (ClassNotFoundException e) {
        ConsoleHelper.writeMessage("Ошибка при обмене данными с удаленным адресом");
    }
            connectionMap.remove(clientName);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED,clientName));
            ConsoleHelper.writeMessage("Соединение закрыто!");
        }
    }




    public static void sendBroadcastMessage(Message message){
        try {

            for (Connection connection : connectionMap.values()) {
                connection.send(message);
            }

        } catch (IOException e){
            //e.printStackTrace();
            ConsoleHelper.writeMessage("Сообщение не отправлено");
        }
    }

    public static void main(String[] args) throws IOException
    {
         int portNumber;
         ConsoleHelper.writeMessage("Введите порт сервера: ");
         portNumber = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            ConsoleHelper.writeMessage("Сервер запущен");

            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                handler.start();
            }
        }catch (Exception e){
            System.out.println("Ошибка работы сервера");
        }
    }
}
