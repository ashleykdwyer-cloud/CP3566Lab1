import java.sql.*;

public class SearchSafe {
    private static final String url  = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
    private static final String user = "sa";
    private static final String password = "secret";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java SearchSafe <search_term>");
            System.exit(1);
        }

        String searchTerm = args[0];
        DriverManager.setLoginTimeout(5);

        // SAFE: Use PreparedStatement with a parameter for the LIKE pattern to avoid SQL injection
        String sql = "SELECT id, name, program, gpa FROM student WHERE name LIKE ? ORDER BY id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set a query timeout in seconds
            pstmt.setQueryTimeout(10);

            // Bind the parameter with wildcards for the LIKE pattern
            pstmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf("Student #%d · %s · program %s · GPA %.2f%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("program"),
                            rs.getDouble("gpa"));
                }

                if (!found) {
                    System.out.println("(no results)");
                }
            }

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

