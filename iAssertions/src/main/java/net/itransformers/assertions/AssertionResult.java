/*
 * AssertionResult.java
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

package net.itransformers.assertions;


public class AssertionResult {
    private String assertionName;
    private AssertionType type;
    private String description;

    public AssertionResult(String assertionName, AssertionType type) {
        this.assertionName = assertionName;
        this.type = type;
    }

    public AssertionResult(String assertionName, AssertionType type, String description) {
        this.assertionName = assertionName;
        this.type = type;
        this.description = description;
    }

    public AssertionType getType() {
        return type;
    }

    public void setType(AssertionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssertionName() {
        return assertionName;
    }

    public void setAssertionName(String assertionName) {
        this.assertionName = assertionName;
    }

    @Override
    public String toString() {
        return "AssertionResult{" +
                "assertionName='" + assertionName + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}
