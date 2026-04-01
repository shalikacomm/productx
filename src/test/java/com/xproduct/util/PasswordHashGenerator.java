package com.xproduct.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hashes.
 *
 * Run via Maven:
 *   mvn -pl . test -Dtest=PasswordHashGenerator -q
 *
 * Or run the main method directly:
 *   mvn exec:java -Dexec.mainClass="com.xproduct.util.PasswordHashGenerator" \
 *                 -Dexec.args="YourPassword123"
 */
public class PasswordHashGenerator {

    // -------------------------------------------------------
    // MODIFY THIS VALUE to generate a hash for any password
    // -------------------------------------------------------
    private static final String PASSWORD = "ruwan";
    // -------------------------------------------------------

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = (args.length > 0) ? args[0] : PASSWORD;
        String hash = encoder.encode(password);

        System.out.println("=========================================");
        System.out.println("  Password : " + password);
        System.out.println("  Hash     : " + hash);
        System.out.println("=========================================");
        System.out.println("  SQL:");
        System.out.println("  UPDATE users SET password = '" + hash + "' WHERE username = 'admin';");
        System.out.println("=========================================");
    }
}
