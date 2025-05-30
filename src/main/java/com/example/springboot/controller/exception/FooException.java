package com.example.springboot.controller.exception;

public class FooException extends SvcException
{
    public FooException(long id)
    {
        super(id);
    }

    @Override
    public String getMessage()
    {
        return "foo yourself";
    }
}
