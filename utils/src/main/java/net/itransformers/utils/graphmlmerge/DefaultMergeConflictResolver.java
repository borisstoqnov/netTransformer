package net.itransformers.utils.graphmlmerge;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 12/5/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultMergeConflictResolver implements MergeConflictResolver {
    @Override
    public Object resolveConflict(Object srcValue, Object targetValue) {
        if (srcValue == null) {
            return targetValue;
        }
        if (targetValue == null) {
            return srcValue;
        }
        if (targetValue.equals(srcValue)) {
            return targetValue;
        }
        if (targetValue instanceof String && srcValue instanceof String) {
            return targetValue + "," + srcValue;
        } else {
            return targetValue;
        }
    }
}
