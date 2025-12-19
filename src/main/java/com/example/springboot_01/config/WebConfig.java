package com.example.springboot_01.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    public WebConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }
    /**
     * 全局放开跨域（解决 Flutter Web 图片无法访问问题）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径跨域
                .allowedOrigins("*") // 允许所有源访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
    /**
     * 静态资源映射
     * 如果你用的是 static/productImages 或 uploads
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/productImages/**")
                .addResourceLocations("classpath:/static/productImages/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
    }
    /**
     * 如果将来你要加 TokenInterceptor，这里也能用
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 当前你注释掉了，我保持一致
        /*
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/**",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/product/**",
                        "/productImages/**",
                        "/uploads/**"
                );
        */
    }
}

