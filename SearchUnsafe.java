import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SearchUnsafe {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java SearchUnsafe <search_term>");
            System.exit(1);
        }

        String url  = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        String user = "sa";
        String password = "secret";
        String searchTerm = args[0];

        DriverManager.setLoginTimeout(5);

        long startTime = System.nanoTime();

        // UNSAFE: Search term concatenated directly into the SQL query (SQL Injection vulnerability)
        String sql = "SELECT id, name, program, gpa FROM student WHERE name LIKE '%" + searchTerm + "%' ORDER BY id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.setQueryTimeout(10);

            int count = 0;

            try (ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    System.out.printf("Student #%d · %s · program %s · GPA %.2f%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("program"),
                            rs.getDouble("gpa"));
                } else {
                    System.out.printf("No student found with id %d%n", studentId);
                }

            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

            System.out.println();
            System.out.printf("%d student(s) found in %d ms.%n",
                    count, elapsedMs);

            System.exit(0);

        } catch (SQLException e) {
            System.err.println(e.getMessage());

            if (e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("no suitable driver")) {
                System.exit(3);
            }

            System.exit(2);
        }
    }
}

