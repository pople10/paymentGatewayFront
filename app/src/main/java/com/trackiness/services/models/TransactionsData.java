package com.trackiness.services.models;

import java.util.List;

public class TransactionsData {
    Boolean more;
    List<TransactionData> data;

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }

    public List<TransactionData> getData() {
        return data;
    }

    public void setData(List<TransactionData> data) {
        this.data = data;
    }
}
