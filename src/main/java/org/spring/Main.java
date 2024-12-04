package org.spring;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        // Добавление провайдера Bouncy Castle для работы с криптографией
        Security.addProvider(new BouncyCastleProvider());

        // Генерация пары ключей (закрытый и открытый ключи)
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Установка размера ключа в 2048 бит
        KeyPair keyPair = keyPairGenerator.generateKeyPair(); // Генерация пары ключей
        PrivateKey privateKey = keyPair.getPrivate(); // Получение закрытого ключа

        // Определение имени субъекта и издателя сертификата
        X500Name issuer = new X500Name("CN=My Server"); // Имя издателя
        X500Name subject = new X500Name("CN=My Server"); // Имя субъекта

        // Создание сертификата
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, // Имя издателя
                BigInteger.valueOf(System.currentTimeMillis()), // Уникальный номер сертификата
                new Date(System.currentTimeMillis()), // Дата начала действия сертификата
                new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000), // Дата окончания действия сертификата (1 год)
                subject, // Имя субъекта
                keyPair.getPublic() // Открытый ключ
        );

        // Создание подписанта для подписи сертификата с использованием SHA256 и RSA
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
        // Подписание сертификата и его преобразование в объект X509Certificate
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));

        // Создание хранилища ключей (KeyStore) в формате PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null); // Создание нового пустого хранилища ключей
        // Сохранение закрытого ключа и сертификата в хранилище ключей  TODO Замените на ваш пароль
        keyStore.setKeyEntry("myserver", privateKey, "your_keystore_password".toCharArray(), new java.security.cert.Certificate[]{certificate});

        // Сохранение хранилища ключей в файл
        try (FileOutputStream fos = new FileOutputStream("keystore.p12")) { // TODO ПЕРЕМЕСТИТЕ ФАЙЛ
            keyStore.store(fos, "your_keystore_password".toCharArray()); // Запись хранилища в файл  TODO Замените на ваш пароль
        }

        // Вывод сообщения о завершении создания сертификата и хранилища ключей
        System.out.println("Самоподписанный сертификат и хранилище ключей созданы.");
    }
}
