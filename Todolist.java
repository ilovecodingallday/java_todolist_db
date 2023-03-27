import java.sql.*;
import java.util.Scanner;

public class TodoList {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:todo.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY, description TEXT)");

            while (true) {
                System.out.print("Enter a command (add, remove, print, or quit): ");
                String command = scanner.nextLine();

                switch (command) {
                    case "add" -> {
                        System.out.print("Enter a task to add: ");
                        String task = scanner.nextLine();
                        PreparedStatement addStmt = conn.prepareStatement("INSERT INTO tasks (description) VALUES (?)");
                        addStmt.setString(1, task);
                        addStmt.executeUpdate();
                        System.out.println("Task added.");
                    }
                    case "remove" -> {
                        System.out.print("Enter the number of the task to remove: ");
                        int id = scanner.nextInt();
                        scanner.nextLine(); // consume the newline character
                        PreparedStatement removeStmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?");
                        removeStmt.setInt(1, id);
                        int rowsAffected = removeStmt.executeUpdate();
                        if (rowsAffected == 0) {
                            System.out.println("Invalid task number.");
                        } else {
                            System.out.println("Task removed.");
                        }
                    }
                    case "print" -> {
                        System.out.println("\nYour to-do list:");
                        ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");
                        while (rs.next()) {
                            int taskId = rs.getInt("id");
                            String taskDesc = rs.getString("description");
                            System.out.println(taskId + ". " + taskDesc);
                        }
                        rs.close();
                    }
                    case "quit" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid command.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
