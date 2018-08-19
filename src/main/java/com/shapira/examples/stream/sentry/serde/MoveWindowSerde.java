package com.shapira.examples.stream.sentry.serde;


import com.shapira.examples.stream.sentry.model.MoveWindow;
import com.shapira.examples.stream.sentry.serde.base.JsonDeserializer;
import com.shapira.examples.stream.sentry.serde.base.JsonSerializer;
import com.shapira.examples.stream.sentry.serde.base.WrapperSerde;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/18
 */
public class MoveWindowSerde  extends WrapperSerde<MoveWindow> {
    public MoveWindowSerde() {
        super(new JsonSerializer<MoveWindow>(), new JsonDeserializer<MoveWindow>(MoveWindow.class));
    }
}
