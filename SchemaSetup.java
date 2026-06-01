import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Lab 2 of CP3566 Module 2.
 *
 * One-shot setup program. Run this once at the start of a lab session,
 * and again between stages whenever you need a clean slate.
 *
 * What it does:
 *   1. Drops the student table if it already exists.
 *   2. Re-creates it with the schema below.
 *   3. Seeds four rows via a batched PreparedStatement.
 *
 * Expected output: Schema OK · rows seeded: 4
 *
 * H2 creates ./data/studentdb.mv.db on the first run; you do NOT
 * need to issue CREATE DATABASE separately the way you would with
 * MySQL or PostgreSQL.
 */
public class SchemaSetup {
    private static final String url  =
            "jdbc:h2:./data/studentdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
    private static final String user = "sa";
    private static final String password = "secret";

    public static void main(String[] args) throws Exception {
        Object[][] seed = {
                {"Alice Park", "CS", 3.92},
                {"Bob Mercer", "SE", 3.45},
                {"Cara Singh", "IT", 3.78},
                {"Dana Luo",   "DA", 3.61}
        };

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement  stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS student");
            stmt.execute(
                    "CREATE TABLE student (" +
                            "  id      INT AUTO_INCREMENT PRIMARY KEY," +
                            "  name    VARCHAR(80)  NOT NULL," +
                            "  program VARCHAR(12)  NOT NULL," +
                            "  gpa     DECIMAL(3,2) NOT NULL" +
                            ")");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO student (name, program, gpa) VALUES (?, ?, ?)")) {
                for (Object[] row : seed) {
                    ps.setString(1, (String) row[0]);
                    ps.setString(2, (String) row[1]);
                    ps.setDouble(3, (Double) row[2]);
                    ps.addBatch();
                }
                int[] counts = ps.executeBatch();
                System.out.println("Schema OK · rows seeded: " + counts.length);
            }
        }
    }
}
