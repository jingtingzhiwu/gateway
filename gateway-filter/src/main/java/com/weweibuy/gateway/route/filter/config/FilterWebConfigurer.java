package com.weweibuy.gateway.route.filter.config;

import com.weweibuy.gateway.core.advice.ExceptionMatchHandlerComposite;
import com.weweibuy.gateway.core.config.WebConfigurer;
import com.weweibuy.gateway.route.filter.sentinel.SentinelExceptionMatchHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author durenhao
 * @date 2020/2/23 19:06
 **/
@Component
@EnableConfigurationProperties({AuthenticationProperties.class, VerifySignatureProperties.class})
public class FilterWebConfigurer implements WebConfigurer {

    @Override
    public void addExceptionMatchHandler(ExceptionMatchHandlerComposite composite) {
        composite.addHandler(new SentinelExceptionMatchHandler());
    }
}
