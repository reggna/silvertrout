/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav "Gussoh" Sohtell
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package silvertrout.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handles settings for IRC bot
 *
 * Example of proposed xml if found in code of method "createTemplateConfig()"
 *
 *
 * @author Gussoh
 */
public class Settings {

    private File settingsFile = new File("config.xml");
    private List<NetworkSettings> networks = new ArrayList<NetworkSettings>();
    /**
     * Network name -> *Plugin name -> *Config name -> *Config setting
     */
    private Map<String, Map<String, Map<String, String>>> plugins = new HashMap<String, Map<String, Map<String, String>>>();

    /**
     * Network name -> *Channel name -> Password
     */
    private Map<String, Map<String, String>> channels = new HashMap<String, Map<String,String>>();

    /**
     * Creates settings manager with default settings file name
     * @throws silvertrout.settings.Settings.ConfigurationParseException
     */
    public Settings() throws ConfigurationParseException {
        createConfigIfNotFound();
        reload();
    }

    /**
     * Use a sepecific settings file
     * @param settingsFile
     * @throws silvertrout.settings.Settings.ConfigurationParseException
     */
    public Settings(File settingsFile) throws ConfigurationParseException {
        this.settingsFile = settingsFile;
        createConfigIfNotFound();
        reload();
    }

    private void createConfigIfNotFound() throws ConfigurationParseException {
        if (!settingsFile.exists()) {
            try {
                createTemplateConfig();
            } catch (IOException ex) {
                throw new ConfigurationParseException(ex.getMessage(), ex.getCause());
            }
        }
    }

    /**
     * Creates a config template
     * WARNING: Deletes the old config if it exists!
     * @throws IOException
     * @throws ConfigurationParseException
     */
    public void createTemplateConfig() throws IOException, ConfigurationParseException {
        String templateConfig =
                "<silvertrout>\n" +
                "   <network name=\"itstud\" host=\"irc.chalmers.it\" port=\"9999\" username=\"silvertrout\" nickname=\"silvertrout\" realname=\"silvertrout\" ssl=\"true\" charset=\"UTF-8\" password=\"\" enabled=\"true\">\n" +
                "        <plugin name=\"KeepAlive\" />\n" +
                "        <plugin name=\"AdminBoy\">\n" +
                "            <password>password2</password>\n" +
                "        </plugin>\n" +
                "        <plugin name=\"bogus\" enabled=\"false\" />\n" +
                "    </network>\n" +
                "</silvertrout>";
        Writer w = new BufferedWriter(new FileWriter(settingsFile));
        w.write(templateConfig);
        w.close();

        // System.err.print("Config file template \"" + settingsFile.getAbsolutePath() + "\" created!");
        // should we exit with error code?
        throw new ConfigurationParseException("Config file template \"" + settingsFile.getAbsolutePath() + "\" created!");
    }

    /**
     * Reload config file
     * @throws silvertrout.settings.Settings.ConfigurationParseException
     */
    public void reload() throws ConfigurationParseException {
        try {
            readConfig();
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationParseException("Error reading config file, " + ex.getMessage(), ex.getCause());
        } catch (SAXException ex) {
            throw new ConfigurationParseException("Error reading config file, " + ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            throw new ConfigurationParseException("Error reading config file, " + ex.getMessage(), ex.getCause());
        }
    }

    private void readConfig() throws ParserConfigurationException, SAXException, IOException, ConfigurationParseException {
        networks.clear();
        plugins.clear();

        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(settingsFile);
        Element root = d.getDocumentElement();
        if (root == null) {
            throw new ParserConfigurationException("Settings document has no root element");
        }

        // Get the networks
        NodeList nl = root.getElementsByTagName("network");
        for (int i = 0; i < nl.getLength(); i++) {
            Node networkNode = nl.item(i);
            NamedNodeMap nnm = networkNode.getAttributes();

            Node enabledNode = nnm.getNamedItem("enabled");
            Node nameNode = nnm.getNamedItem("name");
            Node hostNode = nnm.getNamedItem("host");
            Node portNode = nnm.getNamedItem("port");
            Node usernameNode = nnm.getNamedItem("username");
            Node nickNode = nnm.getNamedItem("nickname");
            Node realNode = nnm.getNamedItem("realname");
            Node sslNode = nnm.getNamedItem("ssl");
            Node charsetNode = nnm.getNamedItem("charset");
            Node passwordNode = nnm.getNamedItem("password");

            String name;
            String host;
            int port;
            String username;
            String nickname;
            String realname;
            boolean ssl = false;
            String charset = "ISO-8859-1";
            String password = null;

            if (enabledNode != null && enabledNode.getNodeValue().length() > 0) {
                if (!Boolean.parseBoolean(enabledNode.getNodeValue())) {
                    // enabled exists  and  enabled != "true"  and  enabled != ""
                    continue;
                }
            }

            if (nameNode == null) {
                name = "default";
            } else {
                name = nameNode.getNodeValue();
            }

            if (hostNode == null) {
                throw new ConfigurationParseException("Network host for network \"" + name + "\" not set.");
            } else {
                host = hostNode.getNodeValue();
            }

            if (portNode == null || portNode.getNodeValue().length() < 1 || portNode.getNodeValue().equals("0")) {
                port = 6667;
            } else {
                try {
                    port = Integer.parseInt(portNode.getNodeValue());
                    if (port < 1 || port > 65536) {
                        throw new ConfigurationParseException("Port number for network \"" + name + "\" is illegal: " + port);
                    }
                } catch (NumberFormatException e) {
                    throw new ConfigurationParseException("Could not parse port number for network + \"" + name + "\": " + e.getMessage());
                }
            }


            if (usernameNode == null || usernameNode.getNodeValue().length() < 1) {
                username = "silvertrout";
            } else {
                username = usernameNode.getNodeValue();
            }

            if (nickNode == null || nickNode.getNodeValue().length() < 1) {
                nickname = "silvertrout";
            } else {
                nickname = nickNode.getNodeValue();
            }

            if (realNode == null || realNode.getNodeValue().length() < 1) {
                realname = "silvertrout";
            } else {
                realname = realNode.getNodeValue();
            }

            if (sslNode != null && sslNode.getNodeValue().length() > 0 && !sslNode.getNodeValue().equalsIgnoreCase("false")) {
                ssl = true;
            }

            if (charsetNode != null && charsetNode.getNodeValue().length() > 0) {
                charset = charsetNode.getNodeValue();
            }

            if (passwordNode != null && passwordNode.getNodeValue().length() > 0) {
                password = passwordNode.getNodeValue();
            }


            NetworkSettings networkSetting = new NetworkSettings(name, host, port, username, nickname, realname, password, charset, ssl);
            networks.add(networkSetting);
            // Done with network config.

            // Find plugins and channels for network
            Map<String, Map<String, String>> pluginMap = new HashMap<String, Map<String, String>>();
            plugins.put(name, pluginMap);
            Map<String, String> channelMap = new HashMap<String, String>();
            channels.put(name, channelMap);

            NodeList pluginNL = networkNode.getChildNodes();
            for (int j = 0; j < pluginNL.getLength(); j++) {
                Node pluginNode = pluginNL.item(j);
                /* add plugins */
                if (pluginNode.getNodeName().equals("plugin")) {

                    nnm = pluginNode.getAttributes();
                    enabledNode = nnm.getNamedItem("enabled");
                    if (enabledNode != null && enabledNode.getNodeValue().length() > 0) {
                        if (!Boolean.parseBoolean(enabledNode.getNodeValue())) {
                            // enabled exists  and  enabled != "true"  and  enabled != ""
                            continue;
                        }
                    }

                    String pluginName = null;
                    nameNode = nnm.getNamedItem("name");
                    if (nameNode == null || nameNode.getNodeValue().length() < 1) {
                        throw new ConfigurationParseException("Plugin name was not specified in network \"" + name + "\".");
                    } else {
                        pluginName = nameNode.getNodeValue();
                    }

                    Map<String, String> configMap = new HashMap<String, String>();
                    pluginMap.put(pluginName, configMap);

                    NodeList pluginConfigNL = pluginNode.getChildNodes();
                    for (int k = 0; k < pluginConfigNL.getLength(); k++) {
                        Node configNode = pluginConfigNL.item(k);
                        if (configNode.getNodeType() == Node.ELEMENT_NODE) {
                            if (configNode.getFirstChild() != null) {
                                configMap.put(configNode.getNodeName(), configNode.getFirstChild().getNodeValue());
                            } else {
                                configMap.put(configNode.getNodeName(), new String());
                            }
                        }
                    }
                /* add channels */
                } else if (pluginNode.getNodeName().equals("channel")) {
                    nnm = pluginNode.getAttributes();
                    nameNode = nnm.getNamedItem("name");
                    passwordNode = nnm.getNamedItem("password");
                    channelMap.put(nameNode.getNodeValue(), passwordNode.getNodeValue());
                }
            }
        }
    }

    /**
     * Get the list of enabled networks
     * @return a list of network settings.
     */
    public List<NetworkSettings> getNetworks() {
        return networks;
    }

    /**
     * Get a list of enabled plugins with their configs for an enabled network
     * *Plugin name -> *Config name -> *Config setting
     * @param networkName
     * @return a list of the plugins to load for this network. or null if there is no such network.
     */
    public Map<String, Map<String, String>> getPluginsFor(String networkName) {
        return plugins.get(networkName);
    }

    /**
     * Get a list of autojoin channels and their password for a specific network
     * @param networkName the name of the network
     * @return a list of all autojoin channels, or null if there is no such network
     */
    public Map<String, String> getChannelsFor(String networkName){
        return channels.get(networkName);
    }

    /**
     * Get all configured plugins for all networks
     * Network name -> *Plugin name -> *Config name -> *Config setting
     * @return
     */
    public Map<String, Map<String, Map<String, String>>> getPlugins() {
        return plugins;
    }

    /**
     * Get all configured channels for all networks
     * Network name -> *Channel name -> Password
     * @return all autojoin channels for all networks
     */
    public Map<String, Map<String, String>> getChannels(){
        return channels;
    }

    /**
     * Get settings for a plugin
     * Returns null if network or plugin does not exist
     * @param networkName
     * @param pluginName
     * @return the settings map or null on no such network or plugin
     */
    public Map<String, String> getPluginSettingsFor(String networkName, String pluginName) {
        Map<String, Map<String, String>> pluginsInNetwork = getPluginsFor(networkName);
        if (pluginsInNetwork != null) {
            return pluginsInNetwork.get(pluginName);
        }
        return null;
    }

    /**
     *
     */
    public class ConfigurationParseException extends Exception {

        /**
         *
         * @param message
         */
        public ConfigurationParseException(String message) {
            super(message);
        }

        /**
         *
         * @param cause
         */
        public ConfigurationParseException(Throwable cause) {
            super(cause);
        }

        /**
         *
         * @param message
         * @param cause
         */
        public ConfigurationParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
