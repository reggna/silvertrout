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
package silvertrout;

import java.util.*;

class Message {

    // Error replies:
    final static int ERR_NOSUCHNICK = 401;
    final static int ERR_NOSUCHSERVER = 402;
    final static int ERR_NOSUCHCHANNEL = 403;
    final static int ERR_CANNOTSENDTOCHAN = 404;
    final static int ERR_TOOMANYCHANNELS = 405;
    final static int ERR_WASNOSUCHNICK = 406;
    final static int ERR_TOOMANYTARGETS = 407;
    final static int ERR_NOORIGIN = 409;
    final static int ERR_NORECIPIENT = 411;
    final static int ERR_NOTEXTTOSEND = 412;
    final static int ERR_NOTOPLEVEL = 413;
    final static int ERR_WILDTOPLEVEL = 414;
    final static int ERR_UNKNOWNCOMMAND = 421;
    final static int ERR_NOMOTD = 422;
    final static int ERR_NOADMININFO = 423;
    final static int ERR_FILEERROR = 424;
    final static int ERR_NONICKNAMEGIVEN = 431;
    final static int ERR_ERRONEUSNICKNAME = 432;
    final static int ERR_NICKNAMEINUSE = 433;
    final static int ERR_NICKCOLLISION = 436;
    final static int ERR_USERNOTINCHANNEL = 441;
    final static int ERR_NOTONCHANNEL = 442;
    final static int ERR_USERONCHANNEL = 443;
    final static int ERR_NOLOGIN = 444;
    final static int ERR_SUMMONDISABLED = 445;
    final static int ERR_USERSDISABLED = 446;
    final static int ERR_NOTREGISTERED = 451;
    final static int ERR_NEEDMOREPARAMS = 461;
    final static int ERR_ALREADYREGISTRED = 462;
    final static int ERR_NOPERMFORHOST = 463;
    final static int ERR_PASSWDMISMATCH = 464;
    final static int ERR_YOUREBANNEDCREEP = 465;
    final static int ERR_KEYSET = 467;
    final static int ERR_CHANNELISFULL = 471;
    final static int ERR_UNKNOWNMODE = 472;
    final static int ERR_INVITEONLYCHAN = 473;
    final static int ERR_BANNEDFROMCHAN = 474;
    final static int ERR_BADCHANNELKEY = 475;
    final static int ERR_NOPRIVILEGES = 481;
    final static int ERR_CHANOPRIVSNEEDED = 482;
    final static int ERR_CANTKILLSERVER = 483;
    final static int ERR_NOOPERHOST = 491;
    final static int ERR_UMODEUNKNOWNFLAG = 501;
    final static int ERR_USERSDONTMATCH = 502;
    // Command responses:
    final static int RPL_NONE = 300;
    final static int RPL_USERHOST = 302;
    final static int RPL_ISON = 303;
    final static int RPL_AWAY = 301;
    final static int RPL_UNAWAY = 305;
    final static int RPL_NOWAWAY = 306;
    final static int RPL_WHOISUSER = 311;
    final static int RPL_WHOISSERVER = 312;
    final static int RPL_WHOISOPERATOR = 313;
    final static int RPL_WHOISIDLE = 317;
    final static int RPL_ENDOFWHOIS = 318;
    final static int RPL_WHOISCHANNELS = 319;
    final static int RPL_WHOWASUSER = 314;
    final static int RPL_ENDOFWHOWAS = 369;
    final static int RPL_LISTSTART = 321;
    final static int RPL_LIST = 322;
    final static int RPL_LISTEND = 323;
    final static int RPL_CHANNELMODEIS = 324;
    final static int RPL_NOTOPIC = 331;
    final static int RPL_TOPIC = 332;
    final static int RPL_INVITING = 341;
    final static int RPL_SUMMONING = 342;
    final static int RPL_VERSION = 351;
    final static int RPL_WHOREPLY = 352;
    final static int RPL_ENDOFWHO = 315;
    final static int RPL_NAMREPLY = 353;
    final static int RPL_ENDOFNAMES = 366;
    final static int RPL_LINKS = 364;
    final static int RPL_ENDOFLINKS = 365;
    final static int RPL_BANLIST = 367;
    final static int RPL_ENDOFBANLIST = 368;
    final static int RPL_INFO = 371;
    final static int RPL_ENDOFINFO = 374;
    final static int RPL_MOTDSTART = 375;
    final static int RPL_MOTD = 372;
    final static int RPL_ENDOFMOTD = 376;
    final static int RPL_YOUREOPER = 381;
    final static int RPL_REHASHING = 382;
    final static int RPL_TIME = 391;
    final static int RPL_USERSSTART = 392;
    final static int RPL_USERS = 393;
    final static int RPL_ENDOFUSERS = 394;
    final static int RPL_NOUSERS = 395;
    final static int RPL_TRACELINK = 200;
    final static int RPL_TRACECONNECTING = 201;
    final static int RPL_TRACEHANDSHAKE = 202;
    final static int RPL_TRACEUNKNOWN = 203;
    final static int RPL_TRACEOPERATOR = 204;
    final static int RPL_TRACEUSER = 205;
    final static int RPL_TRACESERVER = 206;
    final static int RPL_TRACENEWTYPE = 208;
    final static int RPL_TRACELOG = 261;
    final static int RPL_STATSLINKINFO = 211;
    final static int RPL_STATSCOMMANDS = 212;
    final static int RPL_STATSCLINE = 213;
    final static int RPL_STATSNLINE = 214;
    final static int RPL_STATSILINE = 215;
    final static int RPL_STATSKLINE = 216;
    final static int RPL_STATSYLINE = 218;
    final static int RPL_ENDOFSTATS = 219;
    final static int RPL_STATSLLINE = 241;
    final static int RPL_STATSUPTIME = 242;
    final static int RPL_STATSOLINE = 243;
    final static int RPL_STATSHLINE = 244;
    final static int RPL_UMODEIS = 221;
    final static int RPL_LUSERCLIENT = 251;
    final static int RPL_LUSEROP = 252;
    final static int RPL_LUSERUNKNOWN = 253;
    final static int RPL_LUSERCHANNELS = 254;
    final static int RPL_LUSERME = 255;
    final static int RPL_ADMINME = 256;
    final static int RPL_ADMINLOC1 = 257;
    final static int RPL_ADMINLOC2 = 258;
    final static int RPL_ADMINEMAIL = 259;
    // Reserved numerics:
    final static int RPL_TRACECLASS = 209;
    final static int RPL_STATSQLINE = 217;
    final static int RPL_SERVICEINFO = 231;
    final static int RPL_ENDOFSERVICES = 232;
    final static int RPL_SERVICE = 233;
    final static int RPL_SERVLIST = 234;
    final static int RPL_SERVLISTEND = 235;
    final static int RPL_WHOISCHANOP = 316;
    final static int RPL_KILLDONE = 361;
    final static int RPL_CLOSING = 362;
    final static int RPL_CLOSEEND = 363;
    final static int RPL_INFOSTART = 373;
    final static int RPL_MYPORTIS = 384;
    final static int ERR_YOUWILLBEBANNED = 466;
    final static int ERR_BADCHANMASK = 476;
    final static int ERR_NOSERVICEHOST = 492;

    // TODO formatting strings for fat, underline, colors, etc
    // TODO: clean up code
    public String message;
    public String prefix;
    public String command;
    public int reply;
    public String nickname;
    public String username;
    public String host;
    public ArrayList<String> params;

    Message(String data) {

        message = data;
        prefix = null;
        command = null;

        params = new ArrayList<String>();

        Scanner s = new Scanner(data).useDelimiter(" ");
        while (s.hasNext()) {
            String str = s.next();

            if (str.startsWith(":")) {
                if (command == null) {
                    prefix = str.substring(1);
                } else {
                    String trailing = str.substring(1);
                    while (s.hasNext()) {
                        trailing = trailing + " " + s.next();
                    }
                    params.add(trailing);
                    break;
                }
            } else if (command == null) {
                command = str;
            } else {
                params.add(str);
            }

        }

        // Set reply number:
        try {
            reply = Integer.parseInt(command);
        } catch (Exception e) {
            reply = -1;
        }

        // Convert prefix to user info:
        if (prefix != null && prefix.indexOf('!') != -1 && prefix.indexOf('@') != -1) {
            String[] prefixParts = prefix.split("[!@]");
            nickname = prefixParts[0];
            username = prefixParts[1];
            host = prefixParts[2];
        }

        System.out.print("IN  <- | PRFX=" + prefix);

        /*if (nickname != null) {
            System.out.print(" [" + nickname + ", " + username + ", " + host + "]");
        }*/

        if (isReply()) {
            System.out.print(", RPLY=" + reply);
        } else {
            System.out.print(", CMD=" + command);
        }

        System.out.println(", PARAMS=" + params);
        //System.out.println("Data: " + data);
        //System.out.println("");

    }

    public boolean isReply() {
        return (reply != -1);
    }

    public boolean isCommand() {
        return (reply == -1);
    }
}
