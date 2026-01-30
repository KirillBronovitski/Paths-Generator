package org.example.model;

import java.util.List;

public class Node {

    public int[] coordinates;
    public List<Node> neighbors;
    public int distanceValue;
    public List<int[]> pathToNode;

    public Node(int[] coordinates, List<Node> neighbors, int distanceValue, List<int[]> pathToNode) {
        this.coordinates = coordinates;
        this.neighbors = neighbors;
        this.distanceValue = distanceValue;
        this.pathToNode = pathToNode;
    }

}