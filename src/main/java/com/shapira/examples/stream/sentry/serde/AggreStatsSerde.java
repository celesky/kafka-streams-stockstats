package com.shapira.examples.stream.sentry.serde;

import com.shapira.examples.stream.sentry.model.AggreStats;
import com.shapira.examples.stream.sentry.serde.base.JsonDeserializer;
import com.shapira.examples.stream.sentry.serde.base.JsonSerializer;
import com.shapira.examples.stream.sentry.serde.base.WrapperSerde;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/18
 */
public class AggreStatsSerde extends WrapperSerde<AggreStats> {
    public AggreStatsSerde() {
        super(new JsonSerializer<AggreStats>(), new JsonDeserializer<AggreStats>(AggreStats.class));
    }
}