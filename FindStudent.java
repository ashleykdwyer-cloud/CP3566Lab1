import java.sql.*;

public class FindStudent {
    private static final String URL = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";

    public static void main(String[] args) throws Exception {
        int studentId;

        try {
            studentId = Integer.parseInt(args[0]);

            if (studentId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            System.err.printf(
                    "%s | ERROR | FindStudent | message=id must be a positive integer, got \"%s\"%n",
                    java.time.Instant.now(),
                    args[0]
            );
            System.exit(1);
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, "sa", "secret");
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, name, program, gpa FROM student WHERE id = ?")) {

            ps.setInt(1, studentId);   // bind ? at position 1 with the student ID
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("Student #%d · %s · program %s · GPA %.2f%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("program"),
                            rs.getDouble("gpa"));
                } else {
                    System.out.printf("No student found with id %d%n", studentId);
                }
            }
        }
    }
}

