package com.core.api.menu.spring;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Properties;

@Component
public class MinioMessageSource implements MessageSource {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    private final Properties properties = new Properties();

    public MinioMessageSource() {
    }

    private void loadMessages(Locale locale) {
        String objectName = "/product/rx/messages_" + locale.getLanguage() + ".properties";
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        )) {
            properties.load(new java.io.InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            // Fallback to default properties or log an error
            System.err.println("Error loading properties for locale: " + locale);
            e.printStackTrace();
        }
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        if (!properties.containsKey(code)) {
            loadMessages(locale);
        }
        return properties.getProperty(code, defaultMessage);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        if (!properties.containsKey(code)) {
            loadMessages(locale);
        }
        String message = properties.getProperty(code);
        if (message == null) {
            throw new NoSuchMessageException(code, locale);
        }
        return message;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return "";
    }


    public String getMessage(DefaultMessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return getMessage(resolvable.getCode(), resolvable.getArguments(), resolvable.getDefaultMessage(), locale);
    }
}
