package entry.searching_pieces;

import common.datastore.pieces.LongPieces;
import common.pattern.PiecesGenerator;
import core.mino.Block;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class NormalEnumeratePiecesTest {
    @Test
    void enumerateHoldOver1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOver2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(2520);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOver3() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateHoldOverOne() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, S, Z, O, T, J, L");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(1);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateHoldMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(4);
    }

    @Test
    void enumerateNoHoldOver1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOver2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(840);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOver3() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(5040);
    }

    @Test
    void enumerateNoHoldOverOne() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, S, Z, O");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(1);
        assertThat(core.getCounter()).isEqualTo(1);
    }

    @Test
    void enumerateNoHoldMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(2);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateHoldJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p3");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateNoHoldJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p3");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(210);
        assertThat(core.getCounter()).isEqualTo(210);
    }

    @Test
    void enumerateHoldJustMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 5, true);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(4);
    }

    @Test
    void enumerateNoHoldJustMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<LongPieces> pieces = core.enumerate();
        assertThat(pieces).hasSize(3);
        assertThat(core.getCounter()).isEqualTo(3);
    }

    @Test
    void enumerateJustRandomNoHold() throws Exception {
        List<Block> failedBlocks = Arrays.asList(Block.I, Block.O, Block.L, Block.J, Block.S, Block.Z);
        List<Block> allBlocks = new ArrayList<>(Block.valueList());

        for (int size = 1; size <= 7; size++) {
            PiecesGenerator piecesGenerator = new PiecesGenerator("T, *p" + size);
            NormalEnumeratePieces core = new NormalEnumeratePieces(piecesGenerator, size + 1, false);
            Set<LongPieces> pieces = core.enumerate();

            for (int count = 0; count < 1000; count++) {
                List<Block> sample = new ArrayList<>();
                sample.add(Block.T);

                Collections.shuffle(allBlocks);
                sample.addAll(allBlocks.subList(0, size));

                assertThat(new LongPieces(sample)).isIn(pieces);

                for (Block block : failedBlocks) {
                    sample.set(0, block);
                    assertThat(new LongPieces(sample)).isNotIn(pieces);
                }
            }
        }
    }

    @Test
    void enumerateOverRandomNoHold() throws Exception {
        List<Block> failedBlocks = Arrays.asList(Block.I, Block.O, Block.L, Block.J, Block.S, Block.Z);
        List<Block> allBlocks = new ArrayList<>(Block.valueList());

        for (int size = 1; size <= 7; size++) {
            PiecesGenerator piecesGenerator = new PiecesGenerator("T, *p" + size);
            NormalEnumeratePieces core = new NormalEnumeratePieces(piecesGenerator, size, false);
            Set<LongPieces> pieces = core.enumerate();

            for (int count = 0; count < 1000; count++) {
                List<Block> sample = new ArrayList<>();
                sample.add(Block.T);

                Collections.shuffle(allBlocks);
                sample.addAll(allBlocks.subList(0, size - 1));

                assertThat(new LongPieces(sample)).isIn(pieces);

                for (Block block : failedBlocks) {
                    sample.set(0, block);
                    assertThat(new LongPieces(sample)).isNotIn(pieces);
                }
            }
        }
    }
}