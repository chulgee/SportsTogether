package com.iron.dragon.sportstogether;

import com.iron.dragon.sportstogether.abs.Sports;

/**
 * Created by chulchoice on 2016-11-02.
 */
public class ConcreteSports extends Sports {

    protected int id;
    protected String name;

    ConcreteSports(String name, int id){
        this.name = name;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
