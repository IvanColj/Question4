package org.spring;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;

public class Server {
    public static void main(String[] args) {
        try {
            // Загружаем хранилище ключей (keystore) в формате PKCS12
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream keyStoreFile = new FileInputStream("src\\main\\java\\org\\spring\\keystore.p12");
            keyStore.load(keyStoreFile, "your_keystore_password".toCharArray()); // TODO Замените на ваш пароль

            // Создание SSLContext для настройки SSL-соединений
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            // Инициализация KeyManagerFactory с загруженным хранилищем ключей
            kmf.init(keyStore, "your_keystore_password".toCharArray()); // TODO Замените на ваш пароль
            sslContext.init(kmf.getKeyManagers(), null, null); // Инициализация SSLContext с KeyManagers

            // Создание SSLServerSocketFactory для создания SSL-сокетов
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            // Создание SSLServerSocket, который будет слушать на порту 8443
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8443);
            System.out.println("Сервер запущен на порту " + sslServerSocket.getLocalPort());

            // Бесконечный цикл для обработки входящих соединений
            while (true) {
                // Принимаем входящее SSL-соединение
                try (SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                     PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true); // Поток для отправки данных клиенту
                     BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()))) { // Поток для получения данных от клиента

                    String inputLine;
                    // Чтение входящих сообщений от клиента
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Получено сообщение: " + inputLine); // Вывод полученного сообщения в консоль
                        // Отправка зашифрованного ответа клиенту
                        out.println("Зашифрованный ответ: " + inputLine.toUpperCase());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace(); // Вывод ошибки в случае исключения
        }
    }
}
