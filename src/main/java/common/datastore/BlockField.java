package common.datastore;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class BlockField implements Comparable<BlockField> {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();
    private static final Field EMPTY_FIELD = FieldFactory.createField(1);

    private final int height;
    private final EnumMap<Block, Field> map;

    public BlockField(int height) {
        this(height, new EnumMap<>(Block.class));
    }

    private BlockField(int height, EnumMap<Block, Field> map) {
        this.height = height;
        this.map = map;
    }

    // TODO: write unittest
    public void setBlock(Block block, int x, int y) {
        assert block != null;
        map.computeIfAbsent(block, b -> FieldFactory.createField(height)).setBlock(x, y);
    }

    public void merge(Field field, Block block) {
        map.computeIfAbsent(block, b -> FieldFactory.createField(height)).merge(field);
    }

    public Field get(Block block) {
        return map.getOrDefault(block, EMPTY_FIELD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockField that = (BlockField) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public int compareTo(BlockField o) {
        for (Block block : Block.values()) {
            Field field = this.map.getOrDefault(block, EMPTY_FIELD);
            Field oField = o.map.getOrDefault(block, EMPTY_FIELD);
            int compare = FIELD_COMPARATOR.compare(field, oField);
            if (compare != 0)
                return compare;
        }
        return 0;
    }

    public boolean containsAll(BlockField target) {
        for (Map.Entry<Block, Field> targetEntry : target.map.entrySet()) {
            Block key = targetEntry.getKey();
            Field targetField = targetEntry.getValue();
            Field myField = this.map.get(key);
            if (!myField.contains(targetField)) {
                return false;
            }
        }
        return true;
    }

    // TODO: write unittest
    public Block getBlock(int x, int y) {
        for (Map.Entry<Block, Field> entry : map.entrySet()) {
            Field field = entry.getValue();
            if (!field.isEmpty(x, y))
                return entry.getKey();
        }
        return null;
    }

    public int getHeight() {
        return height;
    }
}
