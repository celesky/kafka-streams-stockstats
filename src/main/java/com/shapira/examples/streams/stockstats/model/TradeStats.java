package com.shapira.examples.streams.stockstats.model;

/**
 * 每个时间窗口的聚合结果
 */
public class TradeStats {
    // 按照type 和 ticker分组
    String type;
    String ticker;
    int countTrades; // 交易数量 tracking count and sum so we can later calculate avg price
    double sumPrice; // 总价
    double minPrice; // 最低价格
    double avgPrice; // 平均价格

    /**
     * 提供了一个方法对记录进行聚合， TradeStats的 add方怯用于更新窗口内的最低价格、 交易数量和总价 。
     * @param trade
     * @return
     */
    public TradeStats add(Trade trade) {

        if (trade.type == null || trade.ticker == null)
            throw new IllegalArgumentException("Invalid trade to aggregate: " + trade.toString());

        if (this.type == null)
            this.type = trade.type;
        if (this.ticker == null)
            this.ticker = trade.ticker;
        // 如果type不匹配 或者 ticker不匹配 都会抛异常
        // 也就是只能是type 和ticker都匹配  才能放到这个分组
        if (!this.type.equals(trade.type) || !this.ticker.equals(trade.ticker))
            throw new IllegalArgumentException("Aggregating stats for trade type " + this.type + " and ticker " + this.ticker + " but recieved trade of type " + trade.type +" and ticker " + trade.ticker );

        if (countTrades == 0){
            this.minPrice = trade.getPrice();
        }
        
        this.countTrades = this.countTrades+1;
        this.sumPrice = this.sumPrice + trade.price;
        this.minPrice = this.minPrice < trade.price ? this.minPrice : trade.price;

        return this;
    }

    public TradeStats computeAvgPrice() {
        this.avgPrice = this.sumPrice / this.countTrades;
        return this;
    }
}
