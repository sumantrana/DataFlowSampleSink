package io.spring.dataflow.sample.usagecostlogger;

public record UsageCostDetail(String userId, double callCost, double dataCost) {
}
