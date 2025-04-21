package com.example.springboot.controller.exception;

public class FooException extends SvcException
{
    @Override
    public String getMessage()
    {  return "foo yourself"; }
}
