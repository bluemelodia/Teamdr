package models;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class TeamRecord extends Model {
    @Id
    String tid;
    @Constraints.Required
    UserAccount[] teamMembers;
    @Constraints.Required
    String teamName;
    @Constraints.Required
    Classes thisClass;

    private static Model.Finder<String, TeamRecord> find = new Model.Finder<>(TeamRecord.class);

    public static List<TeamRecord> findAll() {
        return TeamRecord.find.orderBy("tid").findList();
    }

    public static boolean exists(String tid) {
        return(find.where().eq("tid", tid).findRowCount() == 1) ? true : false;
    }

    public static TeamRecord getTeam(String tid) {
        return find.ref(tid);
    }
}

/// tid, teamMembers, teamName, class