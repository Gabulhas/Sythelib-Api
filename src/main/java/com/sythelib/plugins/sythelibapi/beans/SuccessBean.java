package com.sythelib.plugins.sythelibapi.beans;

import lombok.Value;

@Value
public class SuccessBean
{
    String success;

    public static SuccessBean from(String message)
    {
        return new SuccessBean(message);
    }
}


