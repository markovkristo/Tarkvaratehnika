package ee.ut.math.tvt.salessystem.logic;

import java.awt.*;
import java.util.ArrayList;

public class TeamInfo {
    private String teamName;
    private String teamLeaderName;
    private String teamLeaderEmail;
    private ArrayList<String> teamMembers;
    private Image teamInfoImage;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLeaderName() {
        return teamLeaderName;
    }

    public void setTeamLeaderName(String teamLeaderName) {
        this.teamLeaderName = teamLeaderName;
    }

    public String getTeamLeaderEmail() {
        return teamLeaderEmail;
    }

    public void setTeamLeaderEmail(String teamLeaderEmail) {
        this.teamLeaderEmail = teamLeaderEmail;
    }

    public ArrayList<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public Image getTeamInfoImage() {
        return teamInfoImage;
    }

    public void setTeamInfoImage(Image teamInfoImage) {
        this.teamInfoImage = teamInfoImage;
    }
}
