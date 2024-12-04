package org.spring;

import javax.net.ssl.*;
import java.io.*;
import java.security.cert.X509Certificate;

public class Client {
    public static void main(String[] args) {
        try {
            // Создание SSLContext для обеспечения безопасного соединения
            SSLContext sslContext = SSLContext.getInstance("TLS"); // TLSv1.3/TLSv1.2

            // Инициализация SSLContext с кастомным TrustManager, который игнорирует проверки сертификатов
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null; // Не проверяем удостоверяющие центры
                }
                // Метод для проверки доверия клиентским сертификатам (пустой, игнорируется)
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                // Метод для проверки доверия серверным сертификатам (пустой, игнорируется)
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, null);

            // Получение фабрики SSL-сокетов из SSLContext
            SSLSocketFactory factory = sslContext.getSocketFactory();

            // Создание SSL-сокета для подключения к серверу на localhost и порту 8443
            try (SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8443);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Поток для отправки данных на сервер
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) { // Поток для ввода данных пользователем

                String userMessage;
                while (true) {
                    System.out.print("Введите сообщение для отправки (или 'exit' для выхода): ");
                    userMessage = userInput.readLine(); // Чтение пользовательского ввода

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        System.out.println("Завершение работы клиента.");
                        break;
                    }

                    out.println(userMessage); // Отправка сообщения на сервер

                    // Чтение ответа от сервера
                    String response = in.readLine(); // Ожидаем ответ от сервера
                    System.out.println("Получено зашифрованное сообщение: " + response); // Вывод полученного сообщения в консоль
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Вывод ошибки в случае исключения
        }
    }
}
