# Writing plugins #

# File and directory structure #

In silvertrout the source code and any resources is separated. The source is placed in _source/silvertrout/plugins/pluginname_ and the resources is placed in _resources/silvertrout/plugins/pluginname_.

Your directory name must be in lowercase. You are free to create sub directories and sub packages in this directory if you'd like. This must be done to adhere to the Java standard.

Example:
<pre>
Source location:   source/silvertrout/plugins/testplugin<br>
Resource location: resources/silvertrout/plugins/testplugin<br>
</pre>

# Main file #

Your main is where all the interaction with silvertrout is done, or at least where silvertrout gives you all information. This class must be placed in the silvertrout.plugins.testplugin package.

Example (should be source/silvertrout/plugins/testplugin/TestPlugin.java):
```
package silvertrout.plugins.testplugin;

import silvertrout.*;

public class TestPlugin extends Plugin {
    
    TestPlugin() {
         System.out.println("TestPlugin created");
    }
    
}
```

Our plugin does not actually do anything yet except to tell us when it is created. Next up we will take a look at how to react to things that happen.

# On handlers #

To be able to react to different events on IRC we need to to implement one or several methods - called _on handlers_. There are almost one on handler for each thing that can happen on IRC.

The default on handlers (defined in silvertrout.Plugin) don't do anything. This is because you shouldn't have to implement all on handlers (there are quite a few!) even if you don't use them.

So what on handlers are there? The easiest way to see what on handlers are avaible is to look at the API documentation which can be generated using **ant javadoc** and then look at **documentation/javadoc/index.html**.

We'll go through the basic on handlers here to get you started. First up is the privmsg and message handlers:

```

// Simple function that echoes everything being written to a channel
public void onPrivmsg(Channel channel, User user, String message) {
    channel.sendPrivmsg(User.getNickname() + ": " + message);
}

```

It's actually as simple as that. This function makes your plugin act as an echo. Everything written will be echoed back to the channel, directed to the user.

<pre>
14:31 Tigge> Testing the echo<br>
14:32 Bot> Tigge: Testing the echo<br>
</pre>

# Channels, Users and Network #

  * ...
  * ...

# Commons package #

  * utils
  * games