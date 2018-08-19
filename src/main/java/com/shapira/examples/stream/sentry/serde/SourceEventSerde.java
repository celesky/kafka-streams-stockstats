package com.shapira.examples.stream.sentry.serde;


import com.shapira.examples.stream.sentry.model.SourceEvent;
import com.shapira.examples.stream.sentry.serde.base.JsonDeserializer;
import com.shapira.examples.stream.sentry.serde.base.JsonSerializer;
import com.shapira.examples.stream.sentry.serde.base.WrapperSerde;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/18
 */
public class SourceEventSerde  extends WrapperSerde<SourceEvent> {
    public SourceEventSerde() {
        super(new JsonSerializer<SourceEvent>(), new JsonDeserializer<SourceEvent>(SourceEvent.class));
    }
}
