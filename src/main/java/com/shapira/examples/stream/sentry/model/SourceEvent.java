package com.shapira.examples.stream.sentry.model;

/**
 * @desc: 源事件
 * @author: panqiong
 * @date: 2018/8/10
 */
public class SourceEvent {
    // 监控计划id--->比如 sms监控计划
    private String planId;
    // 统计指标 如:成功/失败/无状态
    private String indecator;
    // 事件事件
    private long timestamp;// 事件发生事件 毫秒
    // 事件数量
    private int count; //默认1 也可以设值 因为原始数据可能是批量的

    public SourceEvent() {

    }

    public SourceEvent(String planId, String indecator, long timestamp, int count) {
        this.planId = planId;
        this.indecator = indecator;
        this.timestamp = timestamp;
        this.count = count;
    }

    public String getPlanId() {
        return planId;
    }

    public SourceEvent setPlanId(String planId) {
        this.planId = planId;
        return this;
    }

    public String getIndecator() {
        return indecator;
    }

    public SourceEvent setIndecator(String indecator) {
        this.indecator = indecator;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SourceEvent setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SourceEvent setCount(int count) {
        this.count = count;
        return this;
    }

    public static void main(String[] args) {
        int num = (int) (Math.random() * 3);
        System.out.println("s = " + num);
    }

    @Override
    public String toString() {
        return "SourceEvent{" +
                "planId='" + planId + '\'' +
                ", indecator='" + indecator + '\'' +
                ", timestamp=" + timestamp +
                ", count=" + count +
                '}';
    }
}
