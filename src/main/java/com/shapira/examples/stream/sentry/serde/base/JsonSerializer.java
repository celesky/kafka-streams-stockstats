package com.shapira.examples.stream.sentry.serde.base;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/12
 */
public class JsonSerializer<T> implements Serializer<T> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, T t) {
        return JSONObject.toJSONString(t).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public void close() {

    }
}