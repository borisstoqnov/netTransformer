package net.itransformers.utils.graphmlmerge;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 12/5/13
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MergeConflictResolver {
    Object resolveConflict(Object srcValue1, Object targetValue);
}
