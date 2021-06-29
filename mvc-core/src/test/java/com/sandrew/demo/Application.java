package com.sandrew.demo;

import com.sandrew.mvc.ServerStarter;

public class Application
{
    public static void main(String[] args)
    {
        try
        {
            ServerStarter.start();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
