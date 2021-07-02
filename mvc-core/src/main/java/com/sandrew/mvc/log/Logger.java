package com.sandrew.mvc.log;

public class Logger
{
    public static void info(String msg, Throwable error)
    {
        System.out.println(msg + "\n\r error:" + error.getMessage());
    }

    public static void info(String msg)
    {
        System.out.println(msg);
    }

    public static void debug(String msg, Throwable error)
    {
        System.out.println(msg + "\n\r error:" + error.getMessage());
    }

    public static void debug(String msg)
    {
        System.out.println(msg);
    }

    public static void error(String msg, Throwable error)
    {
        System.err.println(msg + "\n\r error:" + error.getMessage());
    }

    public static void error(String msg)
    {
        System.err.println(msg);
    }
}
