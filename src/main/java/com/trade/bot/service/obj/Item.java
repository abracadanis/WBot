package com.trade.bot.service.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("fullName")
    private String name;
    @JsonProperty("price")
    private Float price;
    @JsonProperty("fullSlug")
    private String fullSlug;
    @JsonProperty("assetId")
    private Long assetId;
    @JsonProperty("stickers")
    private List<Sticker> stickers;

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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getFullSlug() {
        return fullSlug;
    }

    public void setFullSlug(String fullSlug) {
        this.fullSlug = fullSlug;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
    }
}
