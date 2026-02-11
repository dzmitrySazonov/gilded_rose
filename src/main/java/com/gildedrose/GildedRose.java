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
        it.sellIn = it.sellIn - 1;
        if (it.sellIn < 0) validateAndDecrementQuality(it);
    });

    private ItemProcessor legendaryGoodsProcessor = new ItemProcessor(it -> LEGENDARY_GOODS_NOT_FOR_SALE.contains(it.name.toLowerCase()),
        it -> {});

    private ItemProcessor agedBrieProcessor = new ItemProcessor(it -> it.name != null
        && AGED_BRIE.equalsIgnoreCase(it.name), it -> {
        validateAndIncrementQuality(it);
        it.sellIn = it.sellIn - 1;
        if (it.sellIn < 0) validateAndIncrementQuality(it);
    });

    private ItemProcessor backstagePassesProcessor = new ItemProcessor(it -> it.name != null
        && it.name.startsWith(BACKSTAGE_PASSES_REGEX), it -> {
        if (it.quality < MAX_QUALITY) {
            it.quality = it.quality + 1;
            if (it.sellIn < 11) validateAndIncrementQuality(it);
            if (it.sellIn < 6) validateAndIncrementQuality(it);
        }
        it.sellIn = it.sellIn - 1;
        if (it.sellIn < 0) {
            it.quality = it.quality - it.quality;
        }
    });

    private ItemProcessor conjuredItemsProcessor = new ItemProcessor(it -> it.name != null
        && it.name.startsWith(CONJURED_ITEMS_REGEX), it -> {
        validateAndDecrementQuality(it);
        it.sellIn = it.sellIn - 1;
        validateAndDecrementQuality(it);
    });
    private List<ItemProcessor> customizedItemProcessors = List.of(legendaryGoodsProcessor, agedBrieProcessor, backstagePassesProcessor);


    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {
            var item = items[i];
            customizedItemProcessors.stream()
                .filter(p -> p.checkItem(item))
                .findFirst().orElse(defaultProcessor)
                .processItem(item);
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

    private class ItemProcessor {
        Predicate<Item> testItem;
        Consumer<Item> itemConsumer;

        public ItemProcessor(Predicate<Item> testItem, Consumer<Item> itemConsumer) {
            this.testItem = testItem;
            this.itemConsumer = itemConsumer;
        }

        public boolean checkItem(Item item) {
            return item.name != null && testItem.test(item);
        }

        public void processItem(Item item) {
            itemConsumer.accept(item);
        }
    }
}
