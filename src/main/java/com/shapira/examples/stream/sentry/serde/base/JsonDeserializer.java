package com.shapira.examples.stream.sentry.serde.base;


import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/12
 */
public class JsonDeserializer<T> implements Deserializer<T> {

    private Class<T> deserializedClass;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
    }

    public JsonDeserializer() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> map, boolean b) {
        if(deserializedClass == null) {
            deserializedClass = (Class<T>) map.get("serializedClass");
        }
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if(bytes == null){
            return null;
        }

        return JSONObject.parseObject(new String(bytes),deserializedClass);

    }

    @Override
    public void close() {

    }
}