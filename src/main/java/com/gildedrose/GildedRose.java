package com.gildedrose;

import java.util.ArrayList;
import java.util.List;

class GildedRose {
    private static final int MAX_QUALITY = 50;
    private static final int MIN_QUALITY = 0;
    private final List<ItemUpdater> customizedItemUpdaters = new ArrayList<>();
    private final ItemUpdater defaultUpdater = new DefaultUpdater();

    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
        customizedItemUpdaters.add(new AgedBrieUpdater());
        customizedItemUpdaters.add(new LegendaryUpdater());
        customizedItemUpdaters.add(new BackstagePassesUpdater());
        customizedItemUpdaters.add(new ConjuredUpdater());
    }

    public void updateQuality() {
        for (Item item : items) {
            if (item == null || item.name == null) continue;
            customizedItemUpdaters.stream()
                .filter(p -> p.checkItem(item))
                .findFirst()
                .orElse(defaultUpdater)
                .updateItem(item);
        }
    }

    static void adjustQuality(Item item, int delta) {
        item.quality = Math.max(MIN_QUALITY, Math.min(MAX_QUALITY, item.quality + delta));
    }

    interface ItemUpdater {
        boolean checkItem(Item item);

        void updateItem(Item item);
    }

    static class DefaultUpdater implements ItemUpdater {
        @Override
        public boolean checkItem(Item item) {
            return true;
        }

        @Override
        public void updateItem(Item item) {
            adjustQuality(item, -1);
            item.sellIn--;
            if (item.sellIn < 0) adjustQuality(item, -1);
        }
    }

    static class LegendaryUpdater implements ItemUpdater {
        private static final String SULFURAS_HAND_OF_RAGNAROS = "Sulfuras, Hand of Ragnaros";

        @Override
        public boolean checkItem(Item item) {
            return SULFURAS_HAND_OF_RAGNAROS.equalsIgnoreCase(item.name);
        }

        public void updateItem(Item item) {
            // Legendary items do not change
        }
    }

    static class AgedBrieUpdater implements ItemUpdater {
        private static final String AGED_BRIE = "Aged Brie";

        @Override
        public boolean checkItem(Item item) {
            return AGED_BRIE.equalsIgnoreCase(item.name);
        }

        @Override
        public void updateItem(Item item) {
            adjustQuality(item, 1);
            item.sellIn--;
            if (item.sellIn < 0) adjustQuality(item, 1);
        }
    }

    static class BackstagePassesUpdater implements ItemUpdater {
        private static final String BACKSTAGE_PASSES_REGEX = "Backstage passes";

        @Override
        public boolean checkItem(Item item) {
            return item.name.startsWith(BACKSTAGE_PASSES_REGEX);
        }

        @Override
        public void updateItem(Item item) {
            adjustQuality(item, 1);
            if (item.sellIn < 11) adjustQuality(item, 1);
            if (item.sellIn < 6) adjustQuality(item, 1);
            item.sellIn--;
            if (item.sellIn < 0) item.quality = MIN_QUALITY;
        }
    }

    static class ConjuredUpdater implements ItemUpdater {
        private static final String CONJURED_ITEMS_REGEX = "Conjured";

        @Override
        public boolean checkItem(Item item) {
            return item.name.startsWith(CONJURED_ITEMS_REGEX);
        }

        @Override
        public void updateItem(Item item) {
            adjustQuality(item, -2);
            item.sellIn--;
            if (item.sellIn < 0) adjustQuality(item, -2);
        }
    }

}
