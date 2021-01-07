package com.bemonovoid.playqd;

import com.bemonovoid.playqd.utils.PackageHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = PackageHelper.BASE_PACKAGE)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
