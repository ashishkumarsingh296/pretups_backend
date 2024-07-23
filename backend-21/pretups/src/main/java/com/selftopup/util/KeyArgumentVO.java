package com.selftopup.util;

import java.io.Serializable;

public class KeyArgumentVO implements Serializable {
    private String _key;
    private String[] _arguments;

    /**
     * @return Returns the arguments.
     */
    public String[] getArguments() {
        return _arguments;
    }

    /**
     * @param arguments
     *            The arguments to set.
     */
    public void setArguments(String[] arguments) {
        _arguments = arguments;
    }

    public void setArguments(String arguments) {
        _arguments = new String[] { arguments };
    }

    /**
     * @return Returns the key.
     */
    public String getKey() {
        return _key;
    }

    /**
     * @param key
     *            The key to set.
     */
    public void setKey(String key) {
        _key = key;
    }
}
