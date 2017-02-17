package com.iron.dragon.sportstogether.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulchoice on 2016-11-02.
 */
public class SportsFactory extends Factory{

    //Map<Integer, Sports> map = new HashMap<Integer, Sports>();
    List<Sports> list = new ArrayList<>();
    int id;

    @Override
    protected Sports createSports(String name) {

        Sports sports = new ConcreteSports(name, id++);
        return sports;
    }

    @Override
    protected void registerSports(Sports sports) {
        list.add(sports);
    }

    @Override
    public List<Sports> getList() {
        return list;
    }
}
