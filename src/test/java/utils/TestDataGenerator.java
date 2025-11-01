package utils;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class for generating unique test data.
 * Helps ensure test isolation by creating unique values for each test run.
 */
public class TestDataGenerator {

    private static final Random random = new Random();
    private static final String[] FIRST_NAMES = {
        "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa",
        "William", "Jennifer", "James", "Mary", "Richard", "Patricia", "Thomas", "Linda"
    };
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas"
    };
    private static final String[] GENDERS = {"Male", "Female", "Other"};

    /**
     * Generates a unique first name with timestamp suffix.
     *
     * @return A unique first name
     */
    public static String generateFirstName() {
        String baseName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        return baseName + "_" + Instant.now().toEpochMilli();
    }

    /**
     * Generates a unique last name with timestamp suffix.
     *
     * @return A unique last name
     */
    public static String generateLastName() {
        String baseName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return baseName + "_" + Instant.now().toEpochMilli();
    }

    /**
     * Generates a random gender.
     *
     * @return A gender string
     */
    public static String generateGender() {
        return GENDERS[random.nextInt(GENDERS.length)];
    }

    /**
     * Generates a unique email address.
     *
     * @return A unique email address
     */
    public static String generateEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@testmail.com";
    }

    /**
     * Generates a unique email with a specific prefix.
     *
     * @param prefix The prefix for the email
     * @return A unique email address
     */
    public static String generateEmail(String prefix) {
        return prefix + "_" + Instant.now().toEpochMilli() + "@testmail.com";
    }

    /**
     * Generates a random IP address.
     *
     * @return A random IP address
     */
    public static String generateIpAddress() {
        return random.nextInt(256) + "." +
               random.nextInt(256) + "." +
               random.nextInt(256) + "." +
               random.nextInt(256);
    }

    /**
     * Generates a unique username.
     *
     * @return A unique username
     */
    public static String generateUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generates a unique username with a specific prefix.
     *
     * @param prefix The prefix for the username
     * @return A unique username
     */
    public static String generateUsername(String prefix) {
        return prefix + "_" + Instant.now().toEpochMilli();
    }

    /**
     * Generates a random password.
     *
     * @return A random password
     */
    public static String generatePassword() {
        return "Pass@" + UUID.randomUUID().toString().substring(0, 12);
    }

    /**
     * Generates a unique test identifier.
     *
     * @return A unique identifier
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a timestamp-based unique suffix.
     *
     * @return A timestamp suffix
     */
    public static String generateTimestampSuffix() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    /**
     * Generates a random integer within a range.
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return A random integer
     */
    public static int generateRandomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * Creates a complete user data object for testing.
     *
     * @return UserData object with random values
     */
    public static UserData generateUserData() {
        return new UserData(
            generateFirstName(),
            generateLastName(),
            generateEmail(),
            generateGender(),
            generateIpAddress()
        );
    }

    /**
     * Creates a user data object with a specific prefix.
     *
     * @param prefix The prefix for identifiable fields
     * @return UserData object with random values
     */
    public static UserData generateUserData(String prefix) {
        return new UserData(
            prefix + "_" + generateFirstName(),
            prefix + "_" + generateLastName(),
            generateEmail(prefix),
            generateGender(),
            generateIpAddress()
        );
    }

    /**
     * Data class to hold user information.
     */
    public static class UserData {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String gender;
        private final String ipAddress;

        public UserData(String firstName, String lastName, String email, String gender, String ipAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
            this.ipAddress = ipAddress;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getGender() {
            return gender;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        @Override
        public String toString() {
            return "UserData{" +
                   "firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   ", email='" + email + '\'' +
                   ", gender='" + gender + '\'' +
                   ", ipAddress='" + ipAddress + '\'' +
                   '}';
        }
    }
}

