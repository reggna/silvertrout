package silvertrout.plugins;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Random;

import silvertrout.Channel;
import silvertrout.User;

/**
 *
 * @author (reggna)
 */
public class RandomQuote extends silvertrout.Plugin {

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if (channel != null) {
            String dir = "jbt/plugins/Logger/" + getNetwork().name;
            String file = channel.getName().substring(1) + ".log";
            if (message.equals("!random")) {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(new File(dir + "/" + file)));
                    int i;
                    for (i = 0; input.readLine() != null; i++) {
                    }
                    input = new BufferedReader(new FileReader(file));
                    Random r = new Random();
                    i = r.nextInt(i);
                    for (int j = 0; j < i; j++) {
                        input.readLine();
                    }
                    channel.sendPrivmsg(input.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
