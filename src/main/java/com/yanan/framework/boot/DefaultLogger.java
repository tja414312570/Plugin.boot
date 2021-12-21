package com.yanan.framework.boot;

import com.yanan.framework.plugin.annotations.*;
import com.yanan.framework.plugin.autowired.plugin.*;
import org.slf4j.*;

@Register(register = { Logger.class }, signlTon = false)
public class DefaultLogger implements CustomProxy<Logger>
{
    public Logger getInstance() {
        final Logger logger = LoggerFactory.getLogger(WiredStackContext.getRegisterDefintion().getRegisterClass());
        return logger;
    }
}
