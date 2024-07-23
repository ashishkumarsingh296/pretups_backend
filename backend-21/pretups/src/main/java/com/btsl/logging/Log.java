package com.btsl.logging;

public interface Log {

    public abstract boolean isDebugEnabled();

    public abstract boolean isErrorEnabled();

    public abstract boolean isFatalEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isTraceEnabled();

    public abstract boolean isWarnEnabled();

    public abstract void trace(Object methodName, Object message);

    public abstract void trace(Object obj, Throwable throwable);

    public abstract void debug(Object methodName, Object message);

    public abstract void debug(Object methodName, Object referenceID, Object message);

    public abstract void debug(Object obj, Throwable throwable);

    public abstract void info(Object methodName, Object message);

    public abstract void info(Object methodName, Object referenceID, Object message);

    public abstract void info(Object obj, Throwable throwable);

    public abstract void warn(Object obj, Throwable throwable);

    public abstract void warn(Object methodName, Object referenceID, Object message);

    public abstract void error(Object methodName, Object message);

    public abstract void error(Object methodName, Object referenceID, Object message);

    public abstract void error(Object obj, Throwable throwable);

    public abstract void errorTrace(Object obj, Throwable throwable);

    public abstract void fatal(Object methodName, Object message);

    public abstract void fatal(Object methodName, Object referenceID, Object message);

    public abstract void fatal(Object obj, Throwable throwable);
}
