package com.gildedrose;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

class GildedRose {
    private static final String AGED_BRIE = "Aged Brie";
    private static final String SULFURAS_HAND_OF_RAGNAROS = "Sulfuras, Hand of Ragnaros";
    private static final String BACKSTAGE_PASSES_REGEX = "Backstage passes";
    private static final String CONJURED_ITEMS_REGEX = "Conjured";
    private static final int MAX_QUALITY = 50;
    private static final int MIN_QUALITY = 0;
    private static final Set<String> LEGENDARY_GOODS_NOT_FOR_SALE = Set.of(SULFURAS_HAND_OF_RAGNAROS.toLowerCase());

    private ItemProcessor defaultProcessor = new ItemProcessor(it -> it.sellIn < MAX_QUALITY + 1, it -> {
        validateAndDecrementQuality(it);
        decrementSellIn(it);
        if (it.sellIn < 0) validateAndDecrementQuality(it);
    });

    private ItemProcessor legendaryGoodsProcessor = new ItemProcessor(it -> LEGENDARY_GOODS_NOT_FOR_SALE.contains(it.name.toLowerCase()),
        it -> {});

    private ItemProcessor agedBrieProcessor = new ItemProcessor(it -> AGED_BRIE.equalsIgnoreCase(it.name), it -> {
        validateAndIncrementQuality(it);
        decrementSellIn(it);
        if (it.sellIn < 0) validateAndIncrementQuality(it);
    });

    private ItemProcessor backstagePassesProcessor = new ItemProcessor(it -> it.name.startsWith(BACKSTAGE_PASSES_REGEX), it -> {
        validateAndIncrementQuality(it);
        if (it.sellIn < 11) validateAndIncrementQuality(it);
        if (it.sellIn < 6) validateAndIncrementQuality(it);
        decrementSellIn(it);
        if (it.sellIn < 0) it.quality = MIN_QUALITY;
    });

    private ItemProcessor conjuredItemsProcessor = new ItemProcessor(it -> it.name.startsWith(CONJURED_ITEMS_REGEX), it -> {
        validateAndDecrementQuality(it, 2);
        decrementSellIn(it);
        if (it.sellIn < 0) validateAndDecrementQuality(it, 2);
    });
    private List<ItemProcessor> customizedItemProcessors = List.of(legendaryGoodsProcessor, agedBrieProcessor, backstagePassesProcessor, conjuredItemsProcessor);

    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {
            var item = items[i];
            customizedItemProcessors.stream()
                .filter(p -> p.checkItem(item))
                .findFirst()
                .orElse(defaultProcessor)
                .processItem(item);
        }
    }

    private void validateAndDecrementQuality(Item item) {
        if (item.quality > MIN_QUALITY) {
            item.quality = item.quality - 1;
        }
    }

    private void validateAndDecrementQuality(Item item, int multiplier) {
        for (int i = 0; i < multiplier; i++) {
            validateAndDecrementQuality(item);
        }
    }

    private void validateAndIncrementQuality(Item item) {
        if (item.quality < MAX_QUALITY) {
            item.quality = item.quality + 1;
        }
    }

    private void decrementSellIn(Item item) {
            item.sellIn = item.sellIn - 1;
    }

    private class ItemProcessor {
        private Predicate<Item> itemPredicate;
        private Consumer<Item> itemConsumer;

        public ItemProcessor(Predicate<Item> itemPredicate, Consumer<Item> itemConsumer) {
            this.itemPredicate = itemPredicate;
            this.itemConsumer = itemConsumer;
        }

        public boolean checkItem(Item item) {
            return item != null && item.name != null && itemPredicate.test(item);
        }

        public void processItem(Item item) {
            itemConsumer.accept(item);
        }
    }
}
