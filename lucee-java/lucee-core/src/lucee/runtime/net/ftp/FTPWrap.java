/**
 *
 * Copyright (c) 2014, the Railo Company Ltd. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package lucee.runtime.net.ftp;

import java.io.IOException;
import java.net.InetAddress;

import lucee.runtime.net.proxy.Proxy;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;


/**
 * Wrap a Client and a Connection
 */
public final class FTPWrap {

    private FTPConnection conn;
    private FTPClient client;
    private InetAddress address;
    private long lastAccess=0;

    /**
	 * @return the lastAccess
	 */
	public long getLastAccess() {
		return lastAccess;
	}



	/**
	 * @param lastAccess the lastAccess to set
	 */
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}



	/**
     * 
     * @param connection
     * @throws IOException
     */
    public FTPWrap(FTPConnection connection) throws IOException {
        this.conn=connection;
        this.address = InetAddress.getByName(connection.getServer());
        connect();        
    }
    
    
    
    /**
     * @return Returns the connection.
     */
    public FTPConnection getConnection() {
        return conn;
    }

    /**
     * @return Returns the client.
     */
    public FTPClient getClient() {
        return client;
    }

    /**
     * @throws IOException
     * 
     */
    public void reConnect() throws IOException { 
        try {
            if(client!=null && client.isConnected())client.disconnect();
        }
        catch(IOException ioe) {}
        connect();
    }
    
    public void reConnect(short transferMode) throws IOException { 
    	if(transferMode!=conn.getTransferMode())
    		((FTPConnectionImpl)conn).setTransferMode(transferMode);
    	reConnect();
    }

    /**
     * connects the client
     * @throws IOException
     */
    private void connect() throws IOException { 
        
        client=new FTPClient();
        
        setConnectionSettings(client,conn);
        
        // transfer mode
        if(conn.getTransferMode()==FTPConstant.TRANSFER_MODE_ASCCI) getClient().setFileType(FTP.ASCII_FILE_TYPE);
        else if(conn.getTransferMode()==FTPConstant.TRANSFER_MODE_BINARY) getClient().setFileType(FTP.BINARY_FILE_TYPE);
        
        
        
        // Connect
        try {
        	Proxy.start(
            		conn.getProxyServer(), 
            		conn.getProxyPort(), 
            		conn.getProxyUser(), 
            		conn.getProxyPassword()
            );
        	client.connect(address,conn.getPort());
        	client.login(conn.getUsername(),conn.getPassword());
        }
        finally {
        	Proxy.end();
        }
    }



	static void setConnectionSettings(FTPClient client, FTPConnection conn) {
		if(client==null) return;
		
		// timeout
        client.setDataTimeout(conn.getTimeout()*1000);
        try {
			client.setSoTimeout(conn.getTimeout()*1000);
		} catch (Throwable t) {}
        
        // passive/active Mode
		int mode = client.getDataConnectionMode();
        if(conn.isPassive()) {
        	if(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE!=mode)
        		client.enterLocalPassiveMode();
        }
        else {
        	if(FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE!=mode)
        		client.enterLocalActiveMode();
        }
	}
}