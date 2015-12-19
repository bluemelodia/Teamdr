package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import com.avaje.ebean.Ebean;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Random;

/**
 * Created by bluemelodia on 11/12/15.
 */
@Entity
public class Notification {

    @Id
    @Constraints.Required
    public int notifID;

    @Constraints.Required
    public String username; // recipient's username

    // COLLAB - received collab request -> send int 1
    // FORMTEAM - successfully formed team -> send int 2
    // BREAKTEAM - broke team of 2 people -> send int 3
    @Constraints.Required
    public int messageType;

    @Constraints.Required
    public String classID; // this class ID

    @Constraints.Required
    public String message; // message varies by type

    @Constraints.Required
    public String teamID;  // team ID of the requester team

    public Boolean disabled = false;

    public Notification(String username, String classID, int type, String teamID, String message) {
        this.username = username;
        this.classID = classID;
        this.messageType = type;
        this.teamID = teamID;
        Random randomGenerator = new Random(); // keep trying to generate a unique notifID
        this.notifID = randomGenerator.nextInt(Integer.MAX_VALUE);
        while (notifExists(this.notifID)) this.notifID = randomGenerator.nextInt(Integer.MAX_VALUE);
        this.message = message;
    }

    public static void createNewNotification(String username, String classID, int type, String teamID, String message) {
        Notification newNotif = new Notification(username, classID, type, teamID, message);
        Ebean.save(newNotif);
    }

    public static List<Notification> getNotifs(String username) {
        return Notification.find.where().eq("username", username).orderBy("classID").findList();
    }

    // silently disable the notification, to avoid the swiping bug
    public static void disableNotifs(String classID, String username) {
        List<Notification> classNotifs = getNotifs(username);
        for (int i = 0; i < classNotifs.size(); i++) {
            Notification thisNotif = classNotifs.get(i);
            thisNotif.disabled = true;
            Ebean.save(thisNotif);
        }
    }

    public static boolean notifExists(int notifID) {
        return (find.where().eq("notifID", notifID).findRowCount() > 0) ? true : false;
    }

    public static boolean hasNotifs(String username) {
        return (find.where().eq("username", username).findRowCount() > 0) ? true : false;
    }

    public static void deleteNotif(int notifID) {
        Notification notif = find.ref(Integer.toString(notifID));
        Ebean.delete(notif);
    }

    public static Notification getThisNotif(int notifID) {
        return find.ref(Integer.toString(notifID));
    }

    // Get the specific collab request that was sent to this user
    public static Notification findNotifID(String username, String classID, String teamID, int messageType) {
        List<Notification> notifList = find.where().eq("username", username).eq("classID", classID).eq("teamID", teamID).findList();
        for (int i = 0; i < notifList.size(); i++) {
            Notification thisNotification = notifList.get(i);
            if (thisNotification.messageType == 1) {
                return thisNotification;
            }
        }
        return null;
    }

    public static int countNotifs(String username) {
        return (find.where().eq("username", username).findRowCount());
    }

    private static Model.Finder<String, Notification> find = new Model.Finder<>(Notification.class);
}