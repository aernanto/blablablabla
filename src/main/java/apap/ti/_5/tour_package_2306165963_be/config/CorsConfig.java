package apap.ti._5.tour_package_2306165963_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Izinkan SEMUA endpoint
                .allowedOriginPatterns("*") // Izinkan SEMUA asal domain (termasuk localhost)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // Izinkan semua method
                .allowedHeaders("*") // Izinkan semua header
                .allowCredentials(true) // Izinkan kirim kredensial/cookies
                .maxAge(3600);
    }
}