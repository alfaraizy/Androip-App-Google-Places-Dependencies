package com.example.lenovo.nearbylocation.model;

/**
 * Created by LENOVO on 08/06/2018.
 */

public class Periods {
    private Open open;

    private Close close;

    public Open getOpen ()
    {
        return open;
    }

    public void setOpen (Open open)
    {
        this.open = open;
    }

    public Close getClose ()
    {
        return close;
    }

    public void setClose (Close close)
    {
        this.close = close;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open = "+open+", close = "+close+"]";
    }
}
