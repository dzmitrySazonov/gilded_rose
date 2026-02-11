package com.gildedrose;

class GildedRose {
    public static final String AGED_BRIE = "Aged Brie";
    public static final String SULFURAS_HAND_OF_RAGNAROS = "Sulfuras, Hand of Ragnaros";
    public static final String BACKSTAGE_PASSES_REGEX = "Backstage passes";
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {
            var itemName = items[i].name;

            if (!itemName.equals(AGED_BRIE) && !itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                if (items[i].quality > 0) {
                    if (!itemName.equals(SULFURAS_HAND_OF_RAGNAROS)) {
                        items[i].quality = items[i].quality - 1;
                    }
                }
            } else {
                if (items[i].quality < 50) {
                    items[i].quality = items[i].quality + 1;

                    if (itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                        if (items[i].sellIn < 11) {
                            validateAndIncrementQuality(items[i]);
                        }

                        if (items[i].sellIn < 6) {
                            validateAndIncrementQuality(items[i]);
                        }
                    }
                }
            }

            if (!itemName.equals(SULFURAS_HAND_OF_RAGNAROS)) {
                items[i].sellIn = items[i].sellIn - 1;
            }

            if (items[i].sellIn < 0) {
                if (!itemName.equals("Aged Brie")) {
                    if (!itemName.startsWith(BACKSTAGE_PASSES_REGEX)) {
                        if (items[i].quality > 0) {
                            if (!itemName.equals(SULFURAS_HAND_OF_RAGNAROS)) {
                                items[i].quality = items[i].quality - 1;
                            }
                        }
                    } else {
                        items[i].quality = items[i].quality - items[i].quality;
                    }
                } else {
                    validateAndIncrementQuality(items[i]);
                }
            }
        }
    }

    private void validateAndIncrementQuality(Item item) {
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }
    }
}
