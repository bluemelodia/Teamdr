package models;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.mvc.Controller;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
/**
 * Created by bluemelodia on 11/13/15.
 */
@Entity
public class Team extends Model {
    @Id
    @Constraints.Required
    String teamID;
    @Constraints.Required
    UserAccount[] teamMembers;
    @Constraints.Required
    String teamName;


}
