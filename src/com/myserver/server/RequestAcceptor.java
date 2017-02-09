package com.myserver.server;

import java.net.Socket;
import java.util.LinkedList;

public class RequestAcceptor
{
	private final int nThreads = 200;
    private final RequestHandler[] threads;
    private final LinkedList queue;
    private SocketProcessor processor;
 
    public RequestAcceptor()
    {
    	ServerContext instance = ServerContext.getInstance();
    	processor = new SocketProcessor(instance);
        queue = new LinkedList();
        threads = new RequestHandler[nThreads];
 
        for (int i=0; i < nThreads; i++) {
            threads[i] = new RequestHandler();
            threads[i].start();
        }
    }
 
    public void execute(Socket acceptedSocket) {
        synchronized(queue) {
            queue.addLast(acceptedSocket);
            queue.notify();
        }
    }
 
    private class RequestHandler extends Thread 
    {
        public void run() 
        {
            Socket socket;
 
            while (true) 
            {
                synchronized(queue) 
                {
                    while (queue.isEmpty()) 
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }
                    socket = (Socket) queue.removeFirst();
                }
                try 
                {
                	processor.processSocket(socket);
                }
                catch (Exception e) 
                {
                	e.printStackTrace();
                    System.err.println("Error executing task");
                }
            }
        }
    }
	
}
