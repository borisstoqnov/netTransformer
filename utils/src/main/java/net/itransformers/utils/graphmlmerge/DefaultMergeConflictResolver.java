/*
 * DefaultMergeConflictResolver.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

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
