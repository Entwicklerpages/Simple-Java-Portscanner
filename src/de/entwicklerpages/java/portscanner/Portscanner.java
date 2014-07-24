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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the Entwicklerpages Simple Java Portscanner.
 * 
 * @author Nicolas Hollmann
 * @version 1.0
 */
public class Portscanner {
	
	/**
	 * The entry point of the whole portscanner
	 * 
	 * @param args	The command line arguments provided by the user
	 */
	public static void main(String[] args) {
		String host;
		int pstart = 0;
		int pend = 1024;
		int timeout = 500;
		int threads = 10;
		boolean outputClosed = false;
		
		List<ScanThread> scanThreads = new ArrayList<ScanThread>();
		EzTimer timer = new EzTimer();
		
		// Don't remove this copyright notice!
		System.out.println("=======================================");
		System.out.println("Entwicklerpages Simple Java Portscanner");
		System.out.println("Copyright 2014, Nicolas Hollmann");
		System.out.println("All rights reserved.");
		System.out.println("License: BSD 2-Clause License");
		System.out.println("=======================================");
		// End of copyright notice.
		
		// Check the command line arguments
		if (args.length < 1)
		{
			System.out.println("Usage: host [start_port] [end_port] [timeout] [threads] [-all]");
			System.out.println("defaults: start = 0, end = 1024, timeout = 500 (ms), threads = 10");
			return;
		}
		host = args[0];
		
		if (args.length >= 2)
			pstart = Integer.parseInt(args[1]);
		
		if (args.length >= 3)
			pend = Integer.parseInt(args[2]);
		
		if (args.length >= 4)
			timeout = Integer.parseInt(args[3]);
		
		if (args.length >= 5)
			threads = Integer.parseInt(args[4]);
		
		if (args.length >= 6 && args[5].equalsIgnoreCase("-all"))
			outputClosed = true;
		
		if (pstart > pend)
		{
			System.out.println("Start port mustn't be greater than end port.");
			return;
		}
		
		// Try to find the ip for the given hostname.
		try {
			InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			System.out.println("Host " + host + " is unknown!");
			return;
		}
		
		// We need 2 threads to avoid zero devision
		if (threads < 2)
			threads = 2;
		
		int count = pend - pstart; // How many ports to scan?
		int rest = count % (threads - 1); 
		int size = (count - rest) / (threads - 1); // Calculate ports per thread
		
		timer.start(); // Start the timer
		
		int i;
		for (i = 0; i < (threads - 1); i++)
		{
			ScanThread scan = new ScanThread(host, pstart + i * size, pstart + (i + 1) * size, timeout, outputClosed);
			scan.start();
			
			scanThreads.add(scan);
		}
		ScanThread scan = new ScanThread(host, pstart + i * size, pend + 1, timeout, outputClosed);
		scan.start();
		scanThreads.add(scan);
		
		// Wait for all threads
		for (ScanThread thread : scanThreads)
		{
			if (thread.isAlive())
			{
				try {
					thread.join();
				} catch (InterruptedException e) {}
			}
		}
		
		// Calculate speed
		long speedMilli = timer.time();
		float speedSec = speedMilli / 1000.0f;
		System.out.println("=======================================");
		System.out.println("Finished in " + speedSec + " seconds.");
		System.out.println("=======================================");
	}

}
