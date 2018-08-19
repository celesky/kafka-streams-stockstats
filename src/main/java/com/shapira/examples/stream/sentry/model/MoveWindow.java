package com.shapira.examples.stream.sentry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desc: 窗口
 * @author: panqiong
 * @date: 2018/8/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveWindow {
    private String planId;
    private int windowId;
    private long startTimestamp;
    private long endTimestamp;
}
