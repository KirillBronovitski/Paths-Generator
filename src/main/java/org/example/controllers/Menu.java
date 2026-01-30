package org.example.controllers;

import org.example.model.Node;
import org.example.services.Maze;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean exit = false;
        String menuText = """
                === Menu ===
                1. Generate a new maze
                2. Load a maze
                0. Exit
                """;
        Maze maze = null;
        while (!exit) {
            if (maze != null) {
                menuText = """
                        === Menu ===
                        1. Generate a new maze
                        2. Load a maze
                        3. Save the maze
                        4. Display the maze
                        5. Find the escape
                        0. Exit
                        """;
            }
            System.out.print(menuText);
            switch (scanner.nextLine()) {
                case "1":
                    maze = createMaze();
                    System.out.println(maze);
                    break;
                case "2":
                    String mazeFile = scanner.nextLine();
                    maze = loadMaze(mazeFile);
                    break;
                case "0":
                    exit = true;
                    System.out.println("Bye!");
                    break;
                case "3":
                    if (maze != null) {
                        saveMaze(maze);
                    }
                    break;
                case "4":
                    if (maze != null) {
                        System.out.println(maze);
                    }
                    break;
                case "5":
                    if (maze != null) {
                        maze.findExit();
                    }
                    break;
                default:
                    System.out.println("Incorrect option. Please try again;");
            }
        }
    }

    private Maze createMaze() {
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the size of a maze");
        String input = scanner.nextLine();
        //"([4-9]|[1-9][0-9]+)\\s+([4-9]|[1-9][0-9]+)")
        while (!input.matches("([4-9]|[1-9][0-9]+)")) {
            System.out.println("Both length and width must be equal or greater than 4");
            input = scanner.nextLine();
        }
        //int[] size = Arrays.stream(input.split("\\s+")).mapToInt(Integer::parseInt).toArray();
        int dimension = Integer.parseInt(input);
        int[] size = new int[]{dimension, dimension};
        //return new Maze(size[1], size[0]);
        return new Maze(size[0], size[1]);
    }

    private Maze loadMaze(String mazeFile) {
        try {
            File file = new File(mazeFile);
            FileReader r = new FileReader(file);
            BufferedReader reader = new BufferedReader(r);
            String[] entranceAndExit = reader.readLine().split("->");
            int[] entrance = Arrays.stream(entranceAndExit[0].split(" ")).mapToInt(Integer::parseInt).toArray();
            int[] exit = Arrays.stream(entranceAndExit[1].split(" ")).mapToInt(Integer::parseInt).toArray();

            StringBuilder mazeMatrixString = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                mazeMatrixString.append(currentLine).append("\n");
                currentLine = reader.readLine();
            }
            int[][] mazeMatrix = Arrays.stream(mazeMatrixString.toString()
                            .split("\n"))
                    .map(str -> Arrays.stream(str.split(" ")).mapToInt(Integer::parseInt).toArray())
                    .toArray(int[][]::new);

            Map<String, Node> adjacencyList = Maze.createAdjacencyList(mazeMatrix, entrance);

            return new Maze(mazeMatrix, adjacencyList, entrance, exit);
        } catch (IOException e) {
            System.out.println("Error");
            return null;
        }

    }

    private void saveMaze(Maze maze) {
        try {
            File mazeFile = new File(scanner.nextLine());
            mazeFile.createNewFile();
            FileWriter w = new FileWriter(mazeFile);
            BufferedWriter writer = new BufferedWriter(w);

            writer.append(maze.getEntranceCoordinates()).append("->").append(maze.getExitCoordinates()).append("\n");
            int[][] mazeMatrix = maze.getMazeMatrix();
            for (int[] matrix : mazeMatrix) {
                for (int i : matrix) {
                    writer.append(String.valueOf(i)).append(" ");
                }
                writer.append("\n");
            }

            writer.flush();
            w.flush();
            writer.close();
            w.close();
        } catch (IOException e) {
            System.out.println("Error: unable to save the maze");
        }
    }

}
