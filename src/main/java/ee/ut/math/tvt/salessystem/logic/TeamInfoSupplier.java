package ee.ut.math.tvt.salessystem.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TeamInfoSupplier {

    public TeamInfoSupplier() {
        try {
            read();
        } catch (IOException e) {
            System.out.println("Something went wrong in TeamInfoSupplier.java");
        }
    }

    public String getTeamName() {
        return getProperty("TEAM_NAME");
    }

    public String getTeamLeaderName() {
        return getProperty("TEAM_CONTACT_PERSON");
    }

    public String getTeamMembers() {
        return getProperty("TEAM_MEMBERS");
    }

    public String getTeamInfoImage() {
        return getProperty("TEAM_LOGO");
    }

    private void read() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        if (inputStream != null) {
            Properties properties = new Properties();
            properties.load(inputStream);
            System.getProperties().putAll(properties);
        } else {
            throw new FileNotFoundException("application.properties file was not found");
        }
    }

    public static String getProperty(String property) {
        return System.getProperty(property);
    }
}
