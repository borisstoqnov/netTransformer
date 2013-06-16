/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
