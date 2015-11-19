package controllers;

import models.*;
import play.mvc.Controller;
import play.data.Form;
import play.mvc.Result;
import views.html.*;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */

public class Classes extends Controller {
    private static final Form<ClassRecord> ClassForm = Form.form(ClassRecord.class);

    public Result leaveClass(String classId) {
        String user = session("connected");

        // Check if the user has a team for this class
        if (TeamRecord.getTeamForClass(user, classId) == null) {
            purgeClass(classId);
            return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
        }

        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classId);
        String[] teamMembers = myTeam.teamMembers.split(" ");
        for (String member: teamMembers) {
            if (member.equals(user)) {
                System.out.println(user + " purged");
                myTeam.teamMembers = myTeam.teamMembers.replace(user, ""); // purge user from team
            }
        }
        if (myTeam.teamMembers.split(" ").length < 1) { // no people left, delete the newly emptied team
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (TeamRecord team: allTeams) { // remove this team from all seen lists
                if (team.tid.equals(myTeam.tid)) continue;
                team.seenTeams.replace(myTeam.tid, "");
            }
            myTeam.delete();
            purgeClass(classId);
            return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
        }

        System.out.println(myTeam.teamMembers);
        myTeam.save();
        System.out.println(user + " left");
        // Notify the rest of the team that you have left the team
        String message = user + " has left team " + myTeam.teamName + " for " + classId;
        for (String member: teamMembers) {
            if (member.equals(user)) continue;
            UserAccount moi = UserAccount.getUser(member);
            Notification.createNewNotification(moi.username, moi.currentClass, 3, myTeam.tid, message);
        }
        purgeClass(classId);
        return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
    }

    public void purgeClass(String classId) {
        String user = session("connected");

        // delete the class from this user's schedule
        UserAccount me = UserAccount.getUser(user);
        String[] allClasses = me.allClasses.split("\\|");
        for (String thisClass: allClasses) {
            if (thisClass.equals(classId)) {
                me.allClasses = me.allClasses.replace(thisClass, "");
            }
        }
        me.save();
    }

    public Result retrieveClass() {
        return ok(classes.render(ClassForm));
    }

    public Result putClass() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<ClassRecord> form = ClassForm.bindFromRequest();
        ClassRecord newClass = form.get();
        // save the data sent through HTTP POST
        System.out.println("newClass: " + newClass.classID + " " + newClass.className);
        newClass.save();

        List<ClassRecord> classes = ClassRecord.findAll();
        for (int i = 0; i < classes.size(); i++) {
            System.out.println(classes.get(i).classID + " " + classes.get(i).className);
        }

        return redirect(routes.Classes.retrieveClass());
    }
}
