package com.gildedrose;

import java.util.Set;

class GildedRose {
    private static final String AGED_BRIE = "Aged Brie";
    private static final String SULFURAS_HAND_OF_RAGNAROS = "Sulfuras, Hand of Ragnaros";
    private static final String BACKSTAGE_PASSES_REGEX = "Backstage passes";
    private static final String CONJURED_ITEMS_REGEX = "Conjured";
    private static final int MAX_QUALITY = 50;
    private static final int MIN_QUALITY = 0;
    private static final Set<String> LEGENDARY_GOODS_NOT_FOR_SALE = Set.of(SULFURAS_HAND_OF_RAGNAROS.toLowerCase());
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = MIN_QUALITY; i < items.length; i++) {
            var item = items[i];
            var itemName = item.name;
            if (LEGENDARY_GOODS_NOT_FOR_SALE.contains(itemName.toLowerCase())) continue;

            if (!itemName.equals(AGED_BRIE) && !itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                validateAndDecrementQuality(item);
            } else {
                if (item.quality < MAX_QUALITY) {
                    item.quality = item.quality + 1;

                    if (itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                        if (item.sellIn < 11) {
                            validateAndIncrementQuality(item);
                        }

                        if (item.sellIn < 6) {
                            validateAndIncrementQuality(item);
                        }
                    }
                }
            }

            item.sellIn = item.sellIn - 1;

            if (item.sellIn < MIN_QUALITY) {
                if (!itemName.equals(AGED_BRIE)) {
                    if (!itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                        validateAndDecrementQuality(item);
                    } else {
                        item.quality = item.quality - item.quality;
                    }
                } else {
                    validateAndIncrementQuality(item);
                }
            }
        }
    }

    private void validateAndDecrementQuality(Item item) {
        if (item.quality > MIN_QUALITY) {
            item.quality = item.quality - 1;
        }
    }

    private void validateAndIncrementQuality(Item item) {
        if (item.quality < MAX_QUALITY) {
            item.quality = item.quality + 1;
        }
    }
}
