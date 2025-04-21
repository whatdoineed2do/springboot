package com.example.springboot.controller.exception;

public class FooException extends Exception 
{
    @Override
    public String getMessage()
    {  return "foo yourself"; }
}
