/******************************************************************************
 * Copyright (c) 2014, Nicolas Hollmann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 ******************************************************************************/
package de.entwicklerpages.java.portscanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Thread class for port scanning.
 * This class execute the portscan for the given port range.
 * 
 * @author Nicolas Hollmann
 * @version 1.0
 */
public class ScanThread extends Thread {
	String host;
	int pStart; // Start port
	int pEnd;   // End port
	int timeout;
	boolean outputClosed;
	
	/**
	 * The constructor.
	 * 
	 * @param host			Host name (f.E. 127.0.0.1 or localhost)
	 * @param pStart		Start port (f.E. 20)
	 * @param pEnd			End port (f.E. 80)
	 * @param timeout		Connection timeout
	 * @param outputClosed	Output closed ports?
	 */
	public ScanThread(String host, int pStart, int pEnd, int timeout, boolean outputClosed)
	{
		this.host = host;
		this.pStart = pStart;
		this.pEnd = pEnd;
		this.timeout = timeout;
		this.outputClosed = outputClosed;
	}
	
	/**
	 * This is the thread entry point.
	 * It loops through the given range and test each port.
	 */
	@Override
	public void run() 
	{
		for (int i = pStart; i < pEnd; i++)
		{
			testPort(i);
		}
	}
	
	/**
	 * This method scan one port.
	 * 
	 * @param port	port number of the target
	 */
	private void testPort(int port)
	{
		try {
			SocketAddress addr = new InetSocketAddress( host, port );
			Socket socket = new Socket();
			socket.connect( addr, timeout );
			System.out.println("PORT " + port + " IS OPEN!");
			socket.close();
		} catch (IOException ex) {
			if (outputClosed)
				System.out.println("PORT " + port + " IS CLOSED.");
		}
	}
	
}
