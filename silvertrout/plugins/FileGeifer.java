package silvertrout.plugins;

import silvertrout.Channel;
import silvertrout.User;


import java.io.*;

public class FileGeifer extends silvertrout.Plugin {

    @Override
    public void onPrivmsg(User from, Channel to, String message) {
        String dir = "/home/tigge/Photos/Pictures";

        if (message.startsWith("!list")) {
            getNetwork().sendPrivmsg(from.getNickname(), "List of files in " + dir + "\n");

            File file = new File(dir);
            String[] fileList = file.list();

            for (int i = 0; i < fileList.length; i++) {
                getNetwork().sendPrivmsg(from.getNickname(), " * " + fileList[i]);
            }

        } else if (message.startsWith("!gief")) {
            File file = new File(dir + "/" + message.substring(6));
            if (file.getParent().equals(dir)) {
                new DCCFileSender(file, from, getNetwork());
            } else {
                System.out.println(file.getAbsoluteFile() + " is not in " + dir);
                System.out.println(file.getParent() + " != " + dir);
            }

        }

    }
}
