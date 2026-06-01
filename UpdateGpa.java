import java.sql.*;

/**
 * UpdateGpa.java — Update a student's GPA in the H2 database.
 *
 * Reads student ID and new GPA from command line arguments, validates them,
 * updates the student's GPA using a PreparedStatement, and reports whether
 * 1 or 0 rows were changed.
 *
 * Usage: java UpdateGpa <student_id> <gpa>
 * Example: java UpdateGpa 1 3.95
 */
public class UpdateGpa {
    private static final String url  = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
    private static final String user = "sa";
    private static final String password = "secret";

    public static void main(String[] args) {
        // Validate command line arguments
        if (args.length != 2) {
            System.err.println("Usage: java UpdateGpa <student_id> <gpa>");
            System.err.println("Example: java UpdateGpa 1 3.95");
            System.exit(1);
        }

        String idStr = args[0];
        String gpaStr = args[1];

        // Validate inputs
        if (!validateInputs(idStr, gpaStr)) {
            System.exit(1);
        }

        DriverManager.setLoginTimeout(5);

        int studentId = Integer.parseInt(idStr);
        double gpa = Double.parseDouble(gpaStr);

        // Update student GPA
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE student SET gpa = ? WHERE id = ?")) {

            ps.setQueryTimeout(10);

            ps.setDouble(1, gpa);
            ps.setInt(2, studentId);

            int rowsChanged = ps.executeUpdate();
            
            if (rowsChanged == 1) {
                System.out.printf(
                        "Updated student #%d · GPA set to %.2f ·  %d row changed%n",
                        studentId,
                        gpa,
                        rowsChanged
                );
            } else {
                System.out.printf(
                        "No update · no student with id %d · %d rows changed%n",
                        studentId,
                        rowsChanged
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(2);
        }
    }

    /**
     * Validates the input parameters.
     *
     * @param idStr  Student ID as a string
     * @param gpaStr GPA as a string
     * @return true if all inputs are valid, false otherwise
     */
    private static boolean validateInputs(String idStr, String gpaStr) {
        // Validate student ID
        try {
            int studentId = Integer.parseInt(idStr);
            if (studentId <= 0) {
                System.err.println("Error: Student ID must be a positive integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Student ID must be a valid integer.");
            return false;
        }

        // Validate GPA
        try {
            double gpa = Double.parseDouble(gpaStr);
            if (gpa < 0.0 || gpa > 4.0) {
                System.err.println("Error: GPA must be between 0.0 and 4.0.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: GPA must be a valid number.");
            return false;
        }

        return true;
    }
}

