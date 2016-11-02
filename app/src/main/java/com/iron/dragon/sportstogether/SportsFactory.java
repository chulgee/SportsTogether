package com.iron.dragon.sportstogether;

import com.iron.dragon.sportstogether.abs.Factory;
import com.iron.dragon.sportstogether.abs.Sports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
