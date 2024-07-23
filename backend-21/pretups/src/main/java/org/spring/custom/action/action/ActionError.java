

package org.spring.custom.action.action;

import java.io.Serializable;


public class ActionError extends ActionMessage implements Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct an action error with no replacement values.</p>
     *
     * @param key Message key for this error message
     */
    public ActionError(String key) {

        super(key);

    }


    /**
     * <p>Construct an action error with the specified replacement values.</p>
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     */
    public ActionError(String key, Object value0) {

        super(key, value0);

    }


    /**
     * <p>Construct an action error with the specified replacement values.</p>
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     */
    public ActionError(String key, Object value0, Object value1) {

        super(key, value0, value1);

    }


    /**
     * <p>Construct an action error with the specified replacement values.</p>
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     * @param value2 Third replacement value
     */
    public ActionError(String key, Object value0, Object value1,
                       Object value2) {

        super(key, value0, value1, value2);

    }


    /**
     * <p>Construct an action error with the specified replacement values.</p>
     *
     * @param key Message key for this error message
     * @param value0 First replacement value
     * @param value1 Second replacement value
     * @param value2 Third replacement value
     * @param value3 Fourth replacement value
     */
    public ActionError(String key, Object value0, Object value1,
                       Object value2, Object value3) {

        super(key, value0, value1, value2, value3);

    }


    /**
     * <p>Construct an action error with the specified replacement values.</p>
     *
     * @param key Message key for this message
     * @param values Array of replacement values
     */
    public ActionError(String key, Object[] values) {

        super(key, values);

    }

}
