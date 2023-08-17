package com.trade.bot.service.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @JsonProperty("i")
    private Long id;
    @JsonProperty("n")
    private String name;
    @JsonProperty("p")
    private Float profit;

    private String firstService;

    private String secondService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getProfit() {
        return profit;
    }

    public void setProfit(Float profit) {
        this.profit = profit;
    }

    public String getFirstService() {
        return firstService;
    }

    public void setFirstService(String firstService) {
        this.firstService = firstService;
    }

    public String getSecondService() {
        return secondService;
    }

    public void setSecondService(String secondService) {
        this.secondService = secondService;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profit=" + profit +
                '}';
    }
}
