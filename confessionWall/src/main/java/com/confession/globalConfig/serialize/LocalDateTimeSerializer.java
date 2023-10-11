package com.confession.globalConfig.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 自定义序列化逻辑
        String formattedDateTime = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        gen.writeString(formattedDateTime);
    }
}