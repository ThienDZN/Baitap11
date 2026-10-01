package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Assignment05AdminCrudSpringBoot4Application_24162120 extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Assignment05AdminCrudSpringBoot4Application_24162120.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Assignment05AdminCrudSpringBoot4Application_24162120.class);
    }
}
