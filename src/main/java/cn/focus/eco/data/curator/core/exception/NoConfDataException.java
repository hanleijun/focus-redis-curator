package cn.focus.eco.data.curator.core.exception;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * no zk configuration data will be treated as an exception
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/4
 */
public class NoConfDataException extends Exception {
    public NoConfDataException(String message) {
        super(message);
    }
}
