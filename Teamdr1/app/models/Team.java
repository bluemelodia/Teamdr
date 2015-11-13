package models;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
/**
 * Created by bluemelodia on 11/13/15.
 */
public class Team {
    @Id
    @Constraints.Required
    String teamID;
    @Constraints.Required
    UserAccount[] teamMembers;
    @Constraints.Required
    String teamName;


}
