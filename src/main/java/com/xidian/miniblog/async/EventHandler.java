package com.xidian.miniblog.async;

import com.xidian.miniblog.entity.Event;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/22 - 17:09
 */
public interface EventHandler {

    void doHandler(Event event);

    List<Integer> getSupportEventTypes();

}
