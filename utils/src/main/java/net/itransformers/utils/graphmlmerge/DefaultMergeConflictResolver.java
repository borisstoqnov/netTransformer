package net.itransformers.utils.graphmlmerge;


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
            if(((String) srcValue).isEmpty()){
                return targetValue;
            }
            else if(((String) targetValue).isEmpty()){
                return srcValue;
            }
            else if(targetValue.equals(srcValue)){
                return targetValue;
            }
            else {
                return targetValue + "," + srcValue;
            }
        } else {
            return targetValue;
        }
    }
}
