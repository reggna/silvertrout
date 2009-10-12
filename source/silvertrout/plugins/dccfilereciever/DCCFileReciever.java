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
package silvertrout.plugins.dccfilereciever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 *
 **
 */
public class DCCFileReciever implements Runnable {

    Socket socket;
    String host;
    int port;
    String name;
    int size;
    OutputStream os;
    InputStream is;
    FileOutputStream fos;

    /**
     *
     * @param name
     * @param size
     * @param host
     * @param port
     */
    public DCCFileReciever(String name, int size, String host, int port) {

        this.name = name;
        this.size = size;
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(host, port);

            socket.setSoTimeout(5000);

            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        new Thread(this).start();
    }

    @Override
    public void run() {

        long bytesRecieved = 0;

        try {
            fos = new FileOutputStream(new File("/tmp/" + name));
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer, 0, 1024)) != -1) {
                bytesRecieved += bytesRead;
                fos.write(buffer, 0, bytesRead);

                byte[] bytes = new byte[4];
                bytes[0] = (byte) ((bytesRecieved & 0xFF000000L) >> 24);
                bytes[1] = (byte) ((bytesRecieved & 0x00FF0000L) >> 16);
                bytes[2] = (byte) ((bytesRecieved & 0x0000FF00L) >> 8);
                bytes[3] = (byte) ((bytesRecieved & 0x000000FFL) >> 0);

                os.write(bytes, 0, 4);
                os.flush();

            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
                socket.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
