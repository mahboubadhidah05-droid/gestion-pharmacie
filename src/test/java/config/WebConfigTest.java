package config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

class WebConfigTest {

    @Test
    void doitConfigurerIntercepteurAuthentification() {

        WebConfig webConfig =
                new WebConfig();

        InterceptorRegistry registry =
                new InterceptorRegistry();

        webConfig.addInterceptors(registry);

        assertNotNull(registry);
    }
}