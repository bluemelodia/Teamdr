package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Random;

/**
 * Created by bluemelodia on 11/12/15.
 */
@Entity
public class Notifications {
    @Id
    @Constraints.Required
    public int notifID;
    @Constraints.Required
    public String username; // recipient's username
    @Constraints.Required
    // COLLAB - received collab request -> send int 1
    // FORMTEAM - successfully formed team -> send int 2
    // BREAKTEAM - broke team of 2 people -> send int 3
    public int type;
    @Constraints.Required
    public String classID; // this class ID
    @Constraints.Required
    public String message; // message varies by type
    @Constraints.Required
    public String teamID;  // team ID of the requester team

    public Notifications(String username, String classID, int type, String teamID, String message) {
        this.username = username;
        this.classID = classID;
        this.type = type;
        Random randomGenerator = new Random(); // keep trying to generate a unique notifID
        this.notifID = randomGenerator.nextInt(Integer.MAX_VALUE);
        while (notifExists(this.notifID)) this.notifID = randomGenerator.nextInt(Integer.MAX_VALUE);
        this.message = message;
    }

    private static Model.Finder<String, Notifications> find = new Model.Finder<>(Notifications.class);

    public static List<Notifications> getNotifs(String username) {
        return Notifications.find.where().eq("username", username).orderBy("classID").findList();
    }

    public static boolean notifExists(int notifID) {
        return (find.where().eq("notifID", notifID).findRowCount() > 0) ? true : false;
    }

    public static boolean hasNotifs(String username) {
        return (find.where().eq("username", username).findRowCount() > 0) ? true : false;
    }
}