package entry.path.output;

import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import core.field.Field;
import core.mino.Piece;
import entry.path.*;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import lib.AsyncBufferedFileWriter;
import searcher.pack.SizedBit;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PatternCSVPathOutput implements PathOutput {
    private static final String FILE_EXTENSION = ".csv";

    private final PathEntryPoint pathEntryPoint;

    private final MyFile outputBaseFile;
    private final ReducePatternGenerator generator;

    public PatternCSVPathOutput(PathEntryPoint pathEntryPoint, PathSettings pathSettings, PatternGenerator generator, int maxDepth) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = pathSettings.getOutputBaseFilePath();
        String namePath = getRemoveExtensionFromPath(outputBaseFilePath);

        // pathが空 または ディレクトリであるとき、pathを追加して、ファイルにする
        if (namePath.isEmpty() || namePath.endsWith(String.valueOf(File.separatorChar)))
            namePath += "path";

        // baseファイル
        String outputFilePath = String.format("%s%s", namePath, FILE_EXTENSION);
        MyFile base = new MyFile(outputFilePath, pathSettings.isResultOutputToConsole());
        base.mkdirs();
        base.verify();

        // 保存
        this.pathEntryPoint = pathEntryPoint;
        this.outputBaseFile = base;
        this.generator = createReduceBlocksGenerator(generator, pathSettings, maxDepth);
    }

    private ReducePatternGenerator createReduceBlocksGenerator(PatternGenerator generator, PathSettings pathSettings, int maxDepth) {
        if (pathSettings.isUsingHold())
            return new ReducePatternGenerator(generator, maxDepth + 1);
        else
            return new ReducePatternGenerator(generator, maxDepth);
    }

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        return path.substring(0, pointIndex);
    }

    @Override
    public void output(PathPairs pathPairs, Field field, SizedBit sizedBit) throws FinderExecuteException {
        List<PathPair> pathPairList = pathPairs.getUniquePathPairList();

        outputLog("Found path = " + pathPairList.size());

        AtomicInteger validCounter = new AtomicInteger();
        AtomicInteger allCounter = new AtomicInteger();

        try (AsyncBufferedFileWriter writer = outputBaseFile.newAsyncWriter()) {
            writer.writeAndNewLine("ツモ,対応地形数,使用ミノ,未使用ミノ,テト譜");

            generator.blocksStream().parallel()
                    .map(blocks -> {
                        // シーケンス名を取得
                        String sequenceName = blocks.blockStream()
                                .map(Piece::getName)
                                .collect(Collectors.joining());

                        // パフェ可能な地形を抽出
                        List<PathPair> valid = pathPairList.stream()
                                .filter(pathPair -> {
                                    HashSet<? extends Pieces> buildBlocks = pathPair.blocksHashSetForPattern();
                                    return buildBlocks.contains(blocks);
                                })
                                .collect(Collectors.toList());

                        // パフェ可能な地形数
                        int possibleSize = valid.size();

                        // パフェ可能ならカウンターをインクリメント
                        allCounter.incrementAndGet();
                        if (0 < possibleSize)
                            validCounter.incrementAndGet();

                        // パフェ可能な地形のテト譜を連結
                        String fumens = valid.stream()
                                .sorted(Comparator.comparing(PathPair::getPatternSize).reversed())
                                .map(pathPair -> "v115@" + pathPair.getFumen())
                                .collect(Collectors.joining(";"));

                        // 使うミノ一覧を抽出
                        Set<PieceCounter> usesSet = valid.stream()
                                .map(PathPair::getBlockCounter)
                                .collect(Collectors.toSet());

                        String uses = usesSet.stream()
                                .map(blockCounter -> blockCounter.getBlockStream()
                                        .sorted()
                                        .map(Piece::getName)
                                        .collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        // 残せるミノ一覧を抽出
                        PieceCounter orderPieceCounter = new PieceCounter(blocks.blockStream());
                        String noUses = usesSet.stream()
                                .map(orderPieceCounter::removeAndReturnNew)
                                .distinct()
                                .map(blockCounter -> blockCounter.getBlockStream()
                                        .sorted()
                                        .map(Piece::getName)
                                        .collect(Collectors.joining()))
                                .collect(Collectors.joining(";"));

                        return String.format("%s,%d,%s,%s,%s", sequenceName, possibleSize, uses, noUses, fumens);
                    })
                    .forEach(writer::writeAndNewLine);

            writer.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        outputLog("");
        outputLog("perfect clear percent");
        outputLog(String.format("  -> success = %.2f%% (%d/%d)", 100.0 * validCounter.get() / allCounter.get(), validCounter.get(), allCounter.get()));
    }

    private void outputLog(String str) throws FinderExecuteException {
        pathEntryPoint.output(str);
    }
}
