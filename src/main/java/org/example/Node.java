package org.example;

import java.util.List;

public class Node {

    int[] coordinates;
    List<Node> neighbors;
    int distanceValue;
    List<int[]> pathToNode;

    Node(int[] coordinates, List<Node> neighbors, int distanceValue, List<int[]> pathToNode) {
        this.coordinates = coordinates;
        this.neighbors = neighbors;
        this.distanceValue = distanceValue;
        this.pathToNode = pathToNode;
    }

}