/*
 * Node.java
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

package net.itransformers.idiscover.api.models.network;

import java.io.Serializable;
import java.util.*;

public class Node implements Serializable{
    private String id;
    private Set<String> aliases;
    private Map<String,Node> aliasToNeighbourMap;
    private Set<Node> neighbours = new HashSet<Node>();

    public Node(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void addNeighbour(Node neighbour){
        neighbours.add(neighbour);
    }

    public Set<Node> getNeighbours() {
        return Collections.unmodifiableSet(neighbours);
    }

    @Override
    public String toString() {
        StringBuilder neighboursStr = new StringBuilder();
        for (Node neighbour : neighbours) {
            neighboursStr.append(neighbour.getId());
            neighboursStr.append(",");
        }
        return "Node{" +
                "id='" + id + '\'' +
                String.format(", neighbours=[%s]",neighboursStr.toString())+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (id != null ? !id.equals(node.id) : node.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public Map<String, Node> getAliasToNeighbourMap() {
        return aliasToNeighbourMap;
    }

    public void setAliasToNeighbourMap(Map<String, Node> aliasToNeighbourMap) {
        this.aliasToNeighbourMap = aliasToNeighbourMap;
    }
}
