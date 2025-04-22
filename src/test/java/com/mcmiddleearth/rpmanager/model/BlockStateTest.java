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

public class BlockStateTest {
    @Test
    public void testParseBlockState() {
        Gson gson = GsonProvider.getGson();
        Reader reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("/blockstates/stone.json"));
        BlockState stone = gson.fromJson(reader, BlockState.class);
        assertNotNull(stone);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("/blockstates/acacia_slab.json"));
        BlockState acaciaSlab = gson.fromJson(reader, BlockState.class);
        assertNotNull(acaciaSlab);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("/blockstates/brown_mushroom_block.json"));
        BlockState brownMushroomBlock = gson.fromJson(reader, BlockState.class);
        assertNotNull(brownMushroomBlock);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("/blockstates/redstone_wire.json"));
        BlockState redstoneWire = gson.fromJson(reader, BlockState.class);
        assertNotNull(redstoneWire);
    }
}
