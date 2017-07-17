package searcher.pack;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;

import java.util.ArrayList;
import java.util.List;

public class InOutPairField {
    public static ColumnSmallField createMaxOuterBoard(SizedBit sizedBit, Field initField) {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        long maxOuterBoard = createMaxOuterBoard(width, height, initField, 9 / width);
        return ColumnFieldFactory.createField(maxOuterBoard);
    }

    public static ColumnSmallField createMaxOuterBoard(int width, int height, Field initField) {
        long maxOuterBoard = createMaxOuterBoard(width, height, initField, 9 / width);
        return ColumnFieldFactory.createField(maxOuterBoard);
    }

    private static long createMaxOuterBoard(int width, int height, Field initField, int max) {
        Field field = initField.freeze(height);

        long board = Long.MAX_VALUE;
        for (int count = 0; count < max - 1; count++) {
            InOutPairField pairField = parseLast(field, width, height);
            ColumnField outerField = pairField.getOuterField();
            board &= outerField.getBoard(0);
            field.slideLeft(width);
        }

        return board;
    }

    public static List<InOutPairField> createInOutPairFields(SizedBit sizedBit, Field initField) {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        return createInOutPairFields(width, height, initField, 9 / width);
    }

    public static List<InOutPairField> createInOutPairFields(int width, int height, Field initField) {
        return createInOutPairFields(width, height, initField, 9 / width);
    }

    private static List<InOutPairField> createInOutPairFields(int width, int height, Field initField, int max) {
        ArrayList<InOutPairField> pairs = new ArrayList<>();

        Field field = initField.freeze(height);
        for (int count = 0; count < max - 1; count++) {
            InOutPairField pairField = parse(field, width, height);
            pairs.add(pairField);
            field.slideLeft(width);
        }

        for (int y = 0; y < height; y++)
            for (int x = (10 - width * (max - 1)); x < width + 3; x++)
                field.setBlock(x, y);

        InOutPairField pairField = parseLast(field, width, height);
        pairs.add(pairField);

        return pairs;
    }

    private static InOutPairField parse(Field field, int width, int height) {
        ColumnSmallField innerField = ColumnFieldFactory.createField();
        ColumnSmallField outerField = ColumnFieldFactory.createField();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!field.isEmpty(x, y))
                    innerField.setBlock(x, y, height);
            }
            for (int x = width; x < width * 2; x++) {
                if (!field.isEmpty(x, y))
                    outerField.setBlock(x, y, height);
            }
        }
        return new InOutPairField(innerField, outerField);
    }

    private static InOutPairField parseLast(Field field, int width, int height) {
        ColumnSmallField innerField = ColumnFieldFactory.createField();
        ColumnSmallField outerField = ColumnFieldFactory.createField();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!field.isEmpty(x, y))
                    innerField.setBlock(x, y, height);
            }
            for (int x = width; x < width + 3; x++) {
                if (!field.isEmpty(x, y))
                    outerField.setBlock(x, y, height);
            }
        }
        return new InOutPairField(innerField, outerField);
    }

    private final ColumnField innerField;
    private final ColumnField outerField;

    public InOutPairField(ColumnField innerField, ColumnField outerField) {
        this.innerField = innerField;
        this.outerField = outerField;
    }

    public ColumnField getOuterField() {
        return outerField;
    }

    public ColumnField getInnerField() {
        return innerField;
    }
}
