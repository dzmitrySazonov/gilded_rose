package com.gildedrose;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

class GildedRose {
    private static final String AGED_BRIE = "Aged Brie";
    private static final String SULFURAS_HAND_OF_RAGNAROS = "Sulfuras, Hand of Ragnaros";
    private static final String BACKSTAGE_PASSES_REGEX = "Backstage passes";
    private static final String CONJURED_ITEMS_REGEX = "Conjured";
    private static final int MAX_QUALITY = 50;
    private static final int MIN_QUALITY = 0;

    private ItemProcessor defaultProcessor = new ItemProcessor(it -> it.sellIn < MAX_QUALITY + 1, it -> {
        adjustQuality(it, -1);
        it.sellIn--;
        if (it.sellIn < 0) adjustQuality(it, -1);
    });

    private ItemProcessor legendaryGoodsProcessor = new ItemProcessor(it -> SULFURAS_HAND_OF_RAGNAROS.equalsIgnoreCase(it.name),
        it -> {
        });

    private ItemProcessor agedBrieProcessor = new ItemProcessor(it -> AGED_BRIE.equalsIgnoreCase(it.name), it -> {
        adjustQuality(it, 1);
        it.sellIn--;
        if (it.sellIn < 0) adjustQuality(it, 1);
    });

    private ItemProcessor backstagePassesProcessor = new ItemProcessor(it -> it.name.startsWith(BACKSTAGE_PASSES_REGEX), it -> {
        adjustQuality(it, 1);
        if (it.sellIn < 11) adjustQuality(it, 1);
        if (it.sellIn < 6) adjustQuality(it, 1);
        it.sellIn--;
        if (it.sellIn < 0) it.quality = MIN_QUALITY;
    });

    private ItemProcessor conjuredItemsProcessor = new ItemProcessor(it -> it.name.startsWith(CONJURED_ITEMS_REGEX), it -> {
        adjustQuality(it, -2);
        it.sellIn--;
        if (it.sellIn < 0) adjustQuality(it, -2);
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

    static void adjustQuality(Item item, int delta) {
        item.quality = Math.max(MIN_QUALITY, Math.min(MAX_QUALITY, item.quality + delta));
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
