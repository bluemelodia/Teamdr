package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */
@Entity
public class Notifications {
    @Id
    @Constraints.Required
    public int notifID;
    @Constraints.Required
    public String username;
    @Constraints.Required
    // COLLAB - received collab request -> send int 1
    // FORMTEAM - successfully formed team -> send int 2
    // BREAKTEAM - broke team of 2 people -> send int 3
    public int type;
    @Constraints.Required
    public String classID;
    @Constraints.Required
    public String message;
    @Constraints.Required
    public String teamID;

    private static int lastNotifID = 0;

    public Notifications(String username, String classID, int type) {
        this.username = username;
        this.classID = classID;
        this.type = type;
        this.notifID = ++lastNotifID; // need to find the next free number to use as the ID
    }

    private static Model.Finder<String, Notifications> find = new Model.Finder<>(Notifications.class);

    public static List<Notifications> getNotifs(String username) {
        return Notifications.find.where().eq("username", username).orderBy("classID").findList();
    }

    public static boolean hasNotifs(String username) {
        return (find.where().eq("username", username).findRowCount() > 0) ? true : false;
    }
}