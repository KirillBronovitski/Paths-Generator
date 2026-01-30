package org.example.services;

import org.example.model.Node;

import java.util.*;

public class Maze {

    private final Random rnd = new Random();
    private final int length;
    private final int width;
    private final int[] entranceCoordinates;
    private int[] exitCoordinates;

    private final Deque<Frame> frames = new ArrayDeque<>();

    record Frame(int[] leafCoordinates, List<String> leaves, List<String> leavesOfCurrent) {
    }

    private final int[][] mazeMatrix;
    Map<String, Node> adjacencyList = new HashMap<>();
    private int nodeCounter = 0;
    private boolean allowExitCreation = false;
    private boolean exitCreated = false;

    private List<int[]> pathToExit;

    public Maze(int length, int width) {
        this.length = length;
        this.width = width;

        mazeMatrix = new int[width][length];
        Arrays.stream(mazeMatrix).forEach(arr -> Arrays.fill(arr, 1));

        this.entranceCoordinates = createEntrance();
        mazeMatrix[entranceCoordinates[0]][entranceCoordinates[1]] = 0;

        frames.offerFirst(new Frame(entranceCoordinates, new ArrayList<>(), new ArrayList<>()));
        while (!frames.isEmpty()) {
            createPaths();
        }
        this.adjacencyList = createAdjacencyList(this.mazeMatrix, this.entranceCoordinates);
    }

    public Maze(int[][] mazeMatrix, Map<String, Node> adjacencyList, int[] entranceCoordinates, int[] exitCoordinates) {
        this.length = mazeMatrix.length;
        this.width = mazeMatrix[0].length;
        this.mazeMatrix = mazeMatrix;
        this.adjacencyList = adjacencyList;
        this.entranceCoordinates = entranceCoordinates;
        this.exitCoordinates = exitCoordinates;
    }

    private int[] createEntrance() {
        int[] borders = new int[]{0, length - 1};
        int yCoordinate = rnd.nextInt(0, width);
        int xCoordinate;
        if (!String.valueOf(yCoordinate).matches(String.format("%d|%d", 0, width - 1))) {
            xCoordinate = borders[rnd.nextInt(0, 2)];
        } else {
            xCoordinate = rnd.nextInt(1, length - 1);
        }
        return new int[]{yCoordinate, xCoordinate};
    }

    private void createPaths() {
        Frame frame = frames.pollFirst();
        if (frame == null) {
            return;
        }

        List<String> leavesOfCurrent = frame.leavesOfCurrent();
        List<String> leaves = frame.leaves();
        int[] nodeCoordinates = frame.leafCoordinates();
        mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 0;

        //try {
        //    System.out.println(this + "\n");
        //    System.out.println(nodeCounter);
        //    Thread.sleep(400);
        //} catch (InterruptedException ignored) {}


        String leaf1 = (nodeCoordinates[0] + 1) + " " + nodeCoordinates[1];
        if (!leaves.contains(leaf1)) {
            leaves.add(leaf1);
            if (!(nodeCoordinates[0] + 1 > width - 1)) {
                leavesOfCurrent.add(leaf1);
            }
        }

        String leaf2 = (nodeCoordinates[0] - 1) + " " + nodeCoordinates[1];
        if (!leaves.contains(leaf2)) {
            leaves.add(leaf2);
            if (!(nodeCoordinates[0] - 1 < 0)) {
                leavesOfCurrent.add(leaf2);
            }
        }

        String leaf3 = nodeCoordinates[0] + " " + (nodeCoordinates[1] + 1);
        if (!leaves.contains(leaf3)) {
            leaves.add(leaf3);
            if (!(nodeCoordinates[1] + 1 > length - 1)) {
                leavesOfCurrent.add(leaf3);
            }
        }

        String leaf4 = nodeCoordinates[0] + " " + (nodeCoordinates[1] - 1);
        if (!leaves.contains(leaf4)) {
            leaves.add(leaf4);
            if (!(nodeCoordinates[1] - 1 < 0)) {
                leavesOfCurrent.add(leaf4);
            }
        }

        if (allowExitCreation) {
            mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 0;
            exitCoordinates = nodeCoordinates;
            allowExitCreation = false;
            exitCreated = true;
            return;
        }

        if (!leavesOfCurrent.isEmpty()) {
            int randomIndex = rnd.nextInt(0, leavesOfCurrent.size());
            for (int i = 0; i < leavesOfCurrent.size(); i++) {

                String leaf = leavesOfCurrent.get(i);
                int[] leafCoordinates = Arrays.stream(leaf.split(" ")).mapToInt(Integer::parseInt).toArray();


                if (leafCoordinates[0] >= mazeMatrix.length - 1 || leafCoordinates[0] == 0 ||
                        leafCoordinates[1] >= mazeMatrix[0].length - 1 || leafCoordinates[1] == 0) {
                    allowExitCreation = nodeCounter > rnd.nextInt(length * width / 10, (length * width) / 10 * 4);
                    if (!allowExitCreation) {
                        leavesOfCurrent.remove(leaf);
                        if (leavesOfCurrent.isEmpty()) {
                            return;
                        }
                        randomIndex = rnd.nextInt(0, leavesOfCurrent.size());
                        i--;
                    } else {
                        randomIndex = i;
                        nodeCounter = -99999;
                        break;
                    }


                } else if ((mazeMatrix[leafCoordinates[0] - 1][leafCoordinates[1] - 1] == 0 && (mazeMatrix[leafCoordinates[0] - 1][leafCoordinates[1]] == 0) == (mazeMatrix[leafCoordinates[0]][leafCoordinates[1] - 1] == 0)) ||
                        (mazeMatrix[leafCoordinates[0] - 1][leafCoordinates[1] + 1] == 0 && (mazeMatrix[leafCoordinates[0] - 1][leafCoordinates[1]] == 0) == (mazeMatrix[leafCoordinates[0]][leafCoordinates[1] + 1] == 0)) ||
                        (mazeMatrix[leafCoordinates[0] + 1][leafCoordinates[1] - 1] == 0 && (mazeMatrix[leafCoordinates[0] + 1][leafCoordinates[1]] == 0) == (mazeMatrix[leafCoordinates[0]][leafCoordinates[1] - 1] == 0)) ||
                        (mazeMatrix[leafCoordinates[0] + 1][leafCoordinates[1] + 1] == 0 && (mazeMatrix[leafCoordinates[0] + 1][leafCoordinates[1]] == 0) == (mazeMatrix[leafCoordinates[0]][leafCoordinates[1] + 1] == 0))) {

                    leavesOfCurrent.remove(leaf);
                    if (leavesOfCurrent.isEmpty()) {
                        return;
                    }
                    randomIndex = rnd.nextInt(0, leavesOfCurrent.size());
                    i--;
                }

            }
            String chosenLeaf = leavesOfCurrent.get(randomIndex);
            int[] leafCoordinates = Arrays.stream(chosenLeaf.split(" ")).mapToInt(Integer::parseInt).toArray();
            nodeCounter++;

            //comment out this if block to pass the tests. Or experiment with this block to edit the mazes :)

            if (exitCreated /*|| nodeCounter > length * width / 100 * 10 */) {
                while ((rnd.nextBoolean()|| rnd.nextBoolean() || rnd.nextBoolean() || rnd.nextBoolean()) && !leavesOfCurrent.isEmpty()) { //this one edit the density of leaves (the greater the chance of true, the smaller the density)
                    leavesOfCurrent.remove(rnd.nextInt(0, leavesOfCurrent.size()));
                }
                if (rnd.nextBoolean() && rnd.nextBoolean() && rnd.nextBoolean()) { //this one edits the consistency of the densities in different areas (the greater the chance, the more uniform the densities are)
                    return;
                }
            }

            leavesOfCurrent.remove(chosenLeaf);
            frames.offerFirst(frame);

            frames.offerFirst(new Frame(leafCoordinates, leaves, new ArrayList<>()/*, nextNode*/));
        }
    }

    public static Map<String, Node> createAdjacencyList(int[][] mazeMatrix, int[] entranceCoordinates) {
        Map<String, Node> adjacencyList = new HashMap<>();
        for (int r = 0; r < mazeMatrix.length; r++) {
            for (int c = 0; c < mazeMatrix[r].length; c++) {
                Node node = new Node(new int[]{r, c}, new ArrayList<>(), Integer.MAX_VALUE, new ArrayList<>());
                adjacencyList.put(node.coordinates[0] + " " + node.coordinates[1], node);
            }
        }
        for (int r = 0; r < mazeMatrix.length; r++) {
            for (int c = 0; c < mazeMatrix[r].length; c++) {
                Node currentNode = adjacencyList.get(r + " " + c);
                if (mazeMatrix[r][c] == 0) {
                    adjacencyList.put(currentNode.coordinates[0] + " " + currentNode.coordinates[1], currentNode);
                    if (currentNode.coordinates[0] + 1 < mazeMatrix.length) {
                        int[] neighbor1 = new int[]{currentNode.coordinates[0] + 1, currentNode.coordinates[1]};
                        if (mazeMatrix[neighbor1[0]][neighbor1[1]] == 0) {
                            Node potentialNeighbor = adjacencyList.get(neighbor1[0] + " " + neighbor1[1]);
                            if (!currentNode.neighbors.contains(potentialNeighbor)) {
                                currentNode.neighbors.add(potentialNeighbor);
                            }
                            if (!potentialNeighbor.neighbors.contains(currentNode)) {
                                potentialNeighbor.neighbors.add(currentNode);
                            }
                        }
                    }

                    if (currentNode.coordinates[0] - 1 >= 0) {
                        int[] neighbor2 = new int[]{currentNode.coordinates[0] - 1, currentNode.coordinates[1]};
                        if (mazeMatrix[neighbor2[0]][neighbor2[1]] == 0) {
                            Node potentialNeighbor = adjacencyList.get(neighbor2[0] + " " + neighbor2[1]);
                            if (!currentNode.neighbors.contains(potentialNeighbor)) {
                                currentNode.neighbors.add(potentialNeighbor);
                            }
                            if (!potentialNeighbor.neighbors.contains(currentNode)) {
                                potentialNeighbor.neighbors.add(currentNode);
                            }
                        }
                    }


                    if (currentNode.coordinates[1] + 1 < mazeMatrix[0].length) {
                        int[] neighbor3 = new int[]{currentNode.coordinates[0], currentNode.coordinates[1] + 1};
                        if (mazeMatrix[neighbor3[0]][neighbor3[1]] == 0) {
                            Node potentialNeighbor = adjacencyList.get(neighbor3[0] + " " + neighbor3[1]);
                            if (!currentNode.neighbors.contains(potentialNeighbor)) {
                                currentNode.neighbors.add(potentialNeighbor);
                            }
                            if (!potentialNeighbor.neighbors.contains(currentNode)) {
                                potentialNeighbor.neighbors.add(currentNode);
                            }
                        }
                    }
                    if (currentNode.coordinates[1] - 1 >= 0) {
                        int[] neighbor4 = new int[]{currentNode.coordinates[0], currentNode.coordinates[1] - 1};
                        if (mazeMatrix[neighbor4[0]][neighbor4[1]] == 0) {
                            Node potentialNeighbor = adjacencyList.get(neighbor4[0] + " " + neighbor4[1]);
                            if (!currentNode.neighbors.contains(potentialNeighbor)) {
                                currentNode.neighbors.add(potentialNeighbor);
                            }
                            if (!potentialNeighbor.neighbors.contains(currentNode)) {
                                potentialNeighbor.neighbors.add(currentNode);
                            }
                        }
                    }
                }
            }
        }
        Node entrance = adjacencyList.get(entranceCoordinates[0] + " " + entranceCoordinates[1]);
        entrance.distanceValue = 0;
        List<int[]> pathToEntrance = new ArrayList<>();
        pathToEntrance.add(entrance.coordinates);
        entrance.pathToNode = pathToEntrance;
        return adjacencyList;
    }

    public void findExit() {
        if (pathToExit != null) {
            for (int[] nodeCoordinates : pathToExit) {
                mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 2;
            }
            System.out.println(this);
            for (int[] nodeCoordinates : pathToExit) {
                mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 0;
            }
            return;
        }
        Queue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distanceValue));

        Queue<Node> closedList = new PriorityQueue<>(Comparator.comparingInt(node -> node.distanceValue));

        priorityQueue.offer(adjacencyList.get(entranceCoordinates[0] + " " + entranceCoordinates[1]));
        Node currentNode = priorityQueue.poll();
        while (currentNode != null) {
            for (Node node : currentNode.neighbors) {
                if (!closedList.contains(node)) {
                    if (node.distanceValue > currentNode.distanceValue) {
                        node.distanceValue = currentNode.distanceValue + 1;
                        node.pathToNode = new ArrayList<>(currentNode.pathToNode);
                        node.pathToNode.add(node.coordinates);
                    }
                    priorityQueue.offer(node);
                }
            }
            closedList.offer(currentNode);
            currentNode = priorityQueue.poll();
        }

        Node exit = adjacencyList.get(exitCoordinates[0] + " " + exitCoordinates[1]);
        for (int[] nodeCoordinates : exit.pathToNode) {
            mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 2;
        }
        System.out.println(this);
        for (int[] nodeCoordinates : exit.pathToNode) {
            mazeMatrix[nodeCoordinates[0]][nodeCoordinates[1]] = 0;
        }
        pathToExit = exit.pathToNode;
    }

    public int[][] getMazeMatrix() {
        return this.mazeMatrix;
    }

    public String getEntranceCoordinates() {
        return this.entranceCoordinates[0] + " " + this.entranceCoordinates[1];
    }

    public String getExitCoordinates() {
        return this.exitCoordinates[0] + " " + this.exitCoordinates[1];
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int[] row : mazeMatrix) {
            for (int i : row) {
                output.append(i == 1 ? "██" : i == 0 ? "  " : "//");
            }
            output.append("\n");
        }
        return output.toString();
    }

}
