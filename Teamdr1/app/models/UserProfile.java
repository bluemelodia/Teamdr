package models;
import com.avaje.ebean.Model;
import controllers.Classes;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */
public class UserProfile {
    @Id
    @Constraints.Required
    String email;
    Classes[] classes;
    String description;
}