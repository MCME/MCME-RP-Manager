import com.google.gson.Gson;
import com.mcmiddleearth.rpmanager.model.BlockState;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BlockStateTest {
    @Test
    public void testParseBlockState() {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("blockstates/stone.json"));
        BlockState stone = gson.fromJson(reader, BlockState.class);
        assertNotNull(stone);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("blockstates/acacia_slab.json"));
        BlockState acaciaSlab = gson.fromJson(reader, BlockState.class);
        assertNotNull(acaciaSlab);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("blockstates/brown_mushroom_block.json"));
        BlockState brownMushroomBlock = gson.fromJson(reader, BlockState.class);
        assertNotNull(brownMushroomBlock);

        reader = new InputStreamReader(BlockStateTest.class.getResourceAsStream("blockstates/redstone_wire.json"));
        BlockState redstoneWire = gson.fromJson(reader, BlockState.class);
        assertNotNull(redstoneWire);
    }
}
