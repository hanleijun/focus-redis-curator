package cn.focus.eco.data.curator.core;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * daemon thread to monitor the configuration center changing state
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/9
 */

@Service
public class WatchService implements InitializingBean {

    @Autowired
    private WatchThread watchTread;

    ScheduledExecutorService WATCH_THREAD = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("watch-pool-%d").daemon(true).build());

    @Override
    public void afterPropertiesSet() throws Exception {
        WATCH_THREAD.execute(watchTread);
    }
}
