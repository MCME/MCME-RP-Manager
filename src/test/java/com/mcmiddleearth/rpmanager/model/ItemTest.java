/*
 * Copyright (C) 2025 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmiddleearth.rpmanager.model;

import com.google.gson.Gson;
import com.mcmiddleearth.rpmanager.model.item.*;
import com.mcmiddleearth.rpmanager.model.item.special.HeadSpecialModel;
import com.mcmiddleearth.rpmanager.model.item.tint.*;
import com.mcmiddleearth.rpmanager.utils.GsonProvider;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {
    @Test
    public void testParseModelItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_model_item.json"));
        Item modelItem = gson.fromJson(reader, Item.class);
        assertNotNull(modelItem);
        modelItemAssertions(modelItem.getModel());
    }

    @Test
    public void testParseConditionItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_condition_item.json"));
        Item conditionItem = gson.fromJson(reader, Item.class);
        assertNotNull(conditionItem);
        conditionItemAssertions(conditionItem.getModel());
    }

    @Test
    public void testParseSelectItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_select_item.json"));
        Item selectItem = gson.fromJson(reader, Item.class);
        assertNotNull(selectItem);
        selectItemAssertions(selectItem.getModel());
    }

    @Test
    public void testParseRangeDispatchItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_range_dispatch_item.json"));
        Item rangeDispatchItem = gson.fromJson(reader, Item.class);
        assertNotNull(rangeDispatchItem);
        rangeDispatchItemAssertions(rangeDispatchItem.getModel());
    }

    @Test
    public void testParseSpecialItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_special_item.json"));
        Item specialItem = gson.fromJson(reader, Item.class);
        assertNotNull(specialItem);
        specialItemAssertions(specialItem.getModel());
    }

    @Test
    public void testParseCompositeItem() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(ItemTest.class.getResourceAsStream("/items/test_composite_item.json"));
        Item compositeItem = gson.fromJson(reader, Item.class);
        assertNotNull(compositeItem);
        assertNotNull(compositeItem.getModel());
        assertTrue(compositeItem.getModel() instanceof CompositeItemsModel);
        assertEquals(5, ((CompositeItemsModel) compositeItem.getModel()).getModels().size());
        modelItemAssertions(((CompositeItemsModel) compositeItem.getModel()).getModels().get(0));
        conditionItemAssertions(((CompositeItemsModel) compositeItem.getModel()).getModels().get(1));
        selectItemAssertions(((CompositeItemsModel) compositeItem.getModel()).getModels().get(2));
        rangeDispatchItemAssertions(((CompositeItemsModel) compositeItem.getModel()).getModels().get(3));
        specialItemAssertions(((CompositeItemsModel) compositeItem.getModel()).getModels().get(4));
    }

    private static void modelItemAssertions(ItemsModel modelItem) {
        assertNotNull(modelItem);
        assertTrue(modelItem instanceof ModelItemsModel);
        assertNotNull(((ModelItemsModel) modelItem).getTints());
        assertEquals(9, ((ModelItemsModel) modelItem).getTints().size());
        assertTrue(((ModelItemsModel) modelItem).getTints().get(0) instanceof ConstantTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(1) instanceof ConstantTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(2) instanceof DyeTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(3) instanceof FireworkTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(4) instanceof GrassTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(5) instanceof MapColorTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(6) instanceof PotionTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(7) instanceof TeamTint);
        assertTrue(((ModelItemsModel) modelItem).getTints().get(8) instanceof CustomModelDataTint);
    }

    private static void conditionItemAssertions(ItemsModel conditionItem) {
        assertNotNull(conditionItem);
        assertTrue(conditionItem instanceof ConditionItemsModel);
        assertNotNull(((ConditionItemsModel) conditionItem).getOnTrue());
        assertTrue(((ConditionItemsModel) conditionItem).getOnTrue() instanceof EmptyItemsModel);
        modelItemAssertions(((ConditionItemsModel) conditionItem).getOnFalse());
    }

    private static void selectItemAssertions(ItemsModel selectItem) {
        assertNotNull(selectItem);
        assertTrue(selectItem instanceof SelectItemsModel);
        assertNotNull(((SelectItemsModel) selectItem).getCases());
        assertEquals(2, ((SelectItemsModel) selectItem).getCases().size());
        assertEquals(1, ((SelectItemsModel) selectItem).getCases().get(0).getWhen().size());
        assertTrue(((SelectItemsModel) selectItem).getCases().get(0).getModel() instanceof EmptyItemsModel);
        assertEquals(2, ((SelectItemsModel) selectItem).getCases().get(1).getWhen().size());
        modelItemAssertions(((SelectItemsModel) selectItem).getCases().get(1).getModel());
        assertTrue(((SelectItemsModel) selectItem).getFallback() instanceof BundleSelectedItemItemsModel);
    }

    private static void rangeDispatchItemAssertions(ItemsModel rangeDispatchItem) {
        assertNotNull(rangeDispatchItem);
        assertTrue(rangeDispatchItem instanceof RangeDispatchItemsModel);
        assertNotNull(((RangeDispatchItemsModel) rangeDispatchItem).getEntries());
        assertEquals(3, ((RangeDispatchItemsModel) rangeDispatchItem).getEntries().size());
        assertTrue(((RangeDispatchItemsModel) rangeDispatchItem).getEntries().get(0).getModel() instanceof EmptyItemsModel);
        modelItemAssertions(((RangeDispatchItemsModel) rangeDispatchItem).getEntries().get(1).getModel());
        assertTrue(((RangeDispatchItemsModel) rangeDispatchItem).getEntries().get(2).getModel() instanceof BundleSelectedItemItemsModel);
        selectItemAssertions(((RangeDispatchItemsModel) rangeDispatchItem).getFallback());
    }

    private static void specialItemAssertions(ItemsModel specialItem) {
        assertNotNull(specialItem);
        assertTrue(specialItem instanceof SpecialItemsModel);
        assertTrue(((SpecialItemsModel) specialItem).getModel() instanceof HeadSpecialModel);
        assertEquals(HeadSpecialModel.Kind.PLAYER, ((HeadSpecialModel) ((SpecialItemsModel) specialItem).getModel()).getKind());
    }
}
