import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ListStudents {
    public static void main(String[] args) {
        String url  = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        String user = "sa";
        String password = "secret";
        // Set login timeout to 5 seconds before calling driver manager
        DriverManager.setLoginTimeout(5);

        long startTime = System.nanoTime();

        String sql = "SELECT id, name, program, gpa FROM student ORDER BY id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            // Set query timeout to 10 seconds before executing statement
            stmt.setQueryTimeout(10);

            int count = 0;

            try (ResultSet rs = stmt.executeQuery(sql)){

                System.out.println(" id │ name             │ prog │   gpa");
                System.out.println("────┼──────────────────┼──────┼──────");

            while (rs.next()) {
                System.out.printf("%3d │ %-16s │ %-4s │ %5.2f%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("program"),
                    rs.getDouble("gpa"));
                count++;
            }
        }
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

            System.out.println();
            System.out.printf("%d student(s) listed in %d ms.%n",
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


