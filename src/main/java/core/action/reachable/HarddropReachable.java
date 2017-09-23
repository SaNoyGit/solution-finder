package core.action.reachable;

import common.datastore.action.Action;
import core.action.cache.MinimalLockedCache;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;

import java.util.List;

/**
 * マルチスレッド非対応
 */
public class HarddropReachable implements Reachable {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinimalLockedCache lockedCache;

    // temporary変数
    private int appearY = 0;

    public HarddropReachable(MinoFactory minoFactory, MinoShifter minoShifter, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.lockedCache = new MinimalLockedCache(maxY);
    }

    // checksを呼び出す前に、Field.cansPutの確認を必ずしていること
    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
        this.appearY = appearY;

        Block block = mino.getBlock();
        Rotate rotate = mino.getRotate();

        if (check(field, block, x, y, rotate))
            return true;

        List<Action> actions = minoShifter.enumerateSameOtherActions(block, rotate, x, y);
        for (Action action : actions)
            if (check(field, block, action.getX(), action.getY(), action.getRotate()))
                return true;

        return false;
    }

    private boolean check(Field field, Block block, int x, int y, Rotate rotate) {
        lockedCache.clear();
        Mino mino = minoFactory.create(block, rotate);
        return check(field, mino, x, y);
    }

    private boolean check(Field field, Mino mino, int x, int y) {
        int maxY = appearY - mino.getMaxY();
        int harddropY = field.getYOnHarddrop(mino, x, maxY);
        return harddropY == y;
    }
}