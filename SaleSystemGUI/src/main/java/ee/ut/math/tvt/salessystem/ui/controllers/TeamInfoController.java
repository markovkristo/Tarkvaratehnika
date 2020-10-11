package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.logic.TeamInfoSupplier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamInfoController implements Initializable {

    @FXML
    private Text teamName;
    @FXML
    private Text teamContactPerson;
    @FXML
    private Text teamMembers;
    @FXML
    private ImageView teamImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showTeamInfo();
    }

    private void showTeamInfo() {
        TeamInfoSupplier teamInfoSupplier = new TeamInfoSupplier();
        teamName.setText(teamInfoSupplier.getTeamName());
        teamContactPerson.setText(teamInfoSupplier.getTeamLeaderName());
        teamMembers.setText(teamInfoSupplier.getTeamMembers());
        teamImage.setImage(new Image(teamInfoSupplier.getTeamInfoImage()));
    }
}
