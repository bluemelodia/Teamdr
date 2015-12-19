package controllers;

import models.*;
import play.mvc.Controller;
import play.data.Form;
import play.mvc.Result;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by bluemelodia on 11/12/15.
 */

public class Classes extends Controller {
    private static final Form<ClassRecord> ClassForm = Form.form(ClassRecord.class);

    public Result leaveClass(String classId) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        // check if class exists, blocks URL hacking
        List<ClassRecord> cs = ClassRecord.findAll();
        boolean found = false;
        for (int i = 0; i < cs.size(); i++) {
            if(classId.equals(cs.get(i).classID)) {
                found = true;
            }
        }
        if (!found) {
            String error = classId + " does not exist.";
            return badRequest(toJson(error));
        }

        // Check if user is currently in this class
        boolean foundClass = false;
        List<String> allClasses = UserAccount.getUser(user).getClassList();
        for (int i = 0; i < allClasses.size(); i++) {
            if (allClasses.get(i).equals(classId)) {
                foundClass = true;
            }
        } if (!foundClass) {
            return redirect(routes.Profile.viewProfile());
        }

        UserAccount userAccount = UserAccount.getUser(user);
        // Check if the user has a team for this class; if not, drop them from the class straight away
        if (TeamRecord.getTeamForClass(user, classId) == null) {
            userAccount.removeClass(classId);
            //userAccount.save();
            String announcement = "You have dropped " + classId;
            return ok(toJson(announcement));
        }
        // User has a team for this class
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classId);
        String oldTeam = myTeam.tid;
        String[] teamMembers = myTeam.teamMembers.split(" ");
        for (String member: teamMembers) {
            if (member.equals(user)) {
                System.out.println(user + " purged");
                myTeam.teamMembers = myTeam.teamMembers.replace(user + " ", ""); // purge user from team
            }
        }
        System.out.println("Team members: " + myTeam.teamMembers);
        System.out.println("User purged"); // TODO: check this works*
        System.out.println("TEAM " + myTeam.tid + " has " + myTeam.teamMembers.trim().length());
        if (myTeam.teamMembers.trim().length() < 1) { // no people left, delete the newly emptied team
            System.out.println("PURGE team: " + myTeam.tid);
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (TeamRecord team: allTeams) { // remove this team from all seen lists
                if (team.tid.equals(myTeam.tid)) continue;
                team.seenTeams.replace(myTeam.tid + " ", "");
            }
            String teamName = myTeam.tid;
            myTeam.delete();
            userAccount.removeClass(classId);
            myTeam.save();
            String announcement = "Team " + teamName + " has disbanded.";

            // remove this team from everyone's seen list
            List<TeamRecord> everyTeam = TeamRecord.findAll();
            for (int i = 0; i < everyTeam.size(); i++) {
                TeamRecord thisTeam = everyTeam.get(i);
                String[] seen = thisTeam.seenTeams.split(" ");
                ArrayList<String> seenTeamsArr = new ArrayList<String>();
                for (int j = 0; j < seen.length; j++) {
                    seenTeamsArr.add(seen[j].trim());
                }
                if (seenTeamsArr.contains(teamName)) {
                    thisTeam.seenTeams = "";
                    for (int k = 0; k < seenTeamsArr.size(); k++) {
                        if (seenTeamsArr.get(k).equals(teamName)) {
                            continue;
                        }
                        thisTeam.seenTeams += seenTeamsArr.get(k) + " ";
                    }
                    thisTeam.save();
                }
            }

            return ok(toJson(announcement));
        }

        System.out.println(myTeam.teamMembers);
        myTeam.save();
        System.out.println(user + " left");
        // Notify the rest of the team that you have left the team
        String message = user + " has left team " + myTeam.teamName + " for " + classId;
        for (String member: teamMembers) {
            if (member.equals(user)) continue;
            System.out.println("I am here");
            UserAccount moi = UserAccount.getUser(member);
            Notification.createNewNotification(moi.username, moi.currentClass, 3, myTeam.tid, message);
        }
        userAccount.removeClass(classId);
        //userAccount.save();
        String announcement = "Because you have dropped " + classId + ", you were removed from team " + oldTeam;
        return ok(toJson(announcement));
    }
    /*
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
    }*/
}
