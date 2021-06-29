package com.sandrew.mvc.exceptions;

public class ParseException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = -4042594687623349469L;

    public ParseException()
    {
        super();
    }

    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ParseException(String message)
    {
        super(message);
    }

    public ParseException(Throwable cause)
    {
        super(cause);
    }
}
