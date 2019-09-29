package com.revolut.mts.dto;

import java.time.Instant;
import java.util.List;

final public class TransactionQuery {
    private Instant fromDate;
    private Instant toDate;
    private String recipient;
    private List<TransactionState> excludeStates;
    private Long offset;
    private Integer limit;
}
