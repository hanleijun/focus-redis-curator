package cn.focus.eco.data.curator.core.constants;

import java.nio.charset.Charset;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * common constants
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2017/12/27
 */
public class Constants {

    public static Charset UTF8 = Charset.forName("UTF-8");

    public static String REDIS_CLUSTER_CONF_PATH = "/config/eco/datasource/redis";

    public static String REDIS_CLUSTER_CONF_NODE = "/config/eco/datasource";

    public static Integer REDIS_CLUSTER_REPLICAS = 6;
}
