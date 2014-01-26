package net.itransformers.utils.graphmlmerge;


public interface MergeConflictResolver {
    Object resolveConflict(Object srcValue1, Object targetValue);
}
