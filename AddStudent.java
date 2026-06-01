import java.sql.*;

/**
 * AddStudent.java — Insert a new student into the H2 database.
 *
 * Reads name, program, and GPA from command line arguments, validates them,
 * inserts the student using a PreparedStatement, and prints the generated
 * student ID.
 *
 * Usage: java AddStudent <name> <program> <gpa>
 * Example: java AddStudent "John Doe" "CS" "3.85"
 */
public class AddStudent {
    private static final String url  = "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
    private static final String user = "sa";
    private static final String password = "secret";



    public static void main(String[] args) {
        // Validate command line arguments
        if (args.length != 3) {
            System.err.println("Usage: java AddStudent <name> <program> <gpa>");
            System.err.println("Example: java AddStudent \"John Doe\" \"CS\" \"3.85\"");
            System.exit(1);
        }

        String name = args[0];
        String program = args[1];
        String gpaStr = args[2];

        // Validate inputs
        if (!validateInputs(name, program, gpaStr)) {
            System.exit(1);
        }

        DriverManager.setLoginTimeout(5);

        double gpa = Double.parseDouble(gpaStr);

        // Insert student and retrieve generated ID
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO student (name, program, gpa) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setQueryTimeout(10);

            ps.setString(1, name);
            ps.setString(2, program);
            ps.setDouble(3, gpa);

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int studentId = rs.getInt(1);

                        System.out.printf(
                                "Inserted student #%d · %s · program %s · GPA %.2f%n",
                                studentId,
                                name,
                                program,
                                gpa
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Validates the input parameters.
     *
     * @param name    Student name
     * @param program Program code
     * @param gpaStr  GPA as a string
     * @return true if all inputs are valid, false otherwise
     */
    private static boolean validateInputs(String name, String program, String gpaStr) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Error: Name cannot be empty.");
            return false;
        }

        if (name.length() > 80) {
            System.err.println("Error: Name must be 80 characters or less.");
            return false;
        }

        // Validate program
        if (program == null || program.trim().isEmpty()) {
            System.err.println("Error: Program cannot be empty.");
            return false;
        }

        if (program.length() > 12) {
            System.err.println("Error: Program must be 12 characters or less.");
            return false;
        }

        if (!program.matches("^[A-Z0-9]{2,12}$")) {
            System.err.println("Error: Program must contain only uppercase letters.");
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

