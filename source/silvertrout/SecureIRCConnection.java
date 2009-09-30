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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *  Create an SSL IRC connection.
 *  This SSL implementations only implements the encryption part of the SSL protocol.
 *  It accepts all certificates, even if they are incorrect or self-signed.
 *
 * @author Gussoh
 */
public class SecureIRCConnection extends IRCConnection {

    /**
     *
     * @param network
     * @throws java.io.IOException
     */
    public SecureIRCConnection(Network network) throws IOException {
        super(network);
    }

    @Override
    protected void connect() throws IOException {
        SSLUtilities su = new SSLUtilities();
        socket = su.getSocketFactory().createSocket(network.getNetworkSettings().getHost(), network.getNetworkSettings().getPort());
    }

    /**
     *
     */
    public final class SSLUtilities {

        private TrustManager[] trustManagers;

        /**
         * Get a socket factory that trusts all certificates.
         * @return
         */
        public SocketFactory getSocketFactory() {
            SSLContext context;

            trustManagers = new TrustManager[]{new TrustingX509TrustManager()};
            
            try {
                context = SSLContext.getInstance("SSL");
                context.init(null, trustManagers, new SecureRandom());
            } catch (GeneralSecurityException gse) {
                throw new IllegalStateException(gse.getMessage());
            }
            return (context.getSocketFactory());
        }

        /**
         *
         */
        public class TrustingX509TrustManager implements X509TrustManager {

            private final X509Certificate[] issuers = new X509Certificate[]{};

            /**
             *
             * @param chain
             * @param authType
             */
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            /**
             *
             * @param chain
             * @param authType
             */
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            /**
             * 
             * @return
             */
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return issuers;
            }
        }
    }
}
