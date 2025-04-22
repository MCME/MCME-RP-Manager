/*
 * Copyright (C) 2021 MCME
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
import com.mcmiddleearth.rpmanager.utils.GsonProvider;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemModelTest {
    @Test
    public void testParseItemModel() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(
                ItemModelTest.class.getResourceAsStream("/models/item/golden_helmet.json"));
        ItemModel goldenHelmet = gson.fromJson(reader, ItemModel.class);
        assertNotNull(goldenHelmet);
    }
}
