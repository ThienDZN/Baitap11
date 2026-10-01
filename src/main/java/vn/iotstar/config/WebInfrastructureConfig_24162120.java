package vn.iotstar.config;

import java.util.EnumSet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.iotstar.controller.AuthController_24162120;
import vn.iotstar.controller.CartController_24162120;
import vn.iotstar.controller.HomeController_24162120;
import vn.iotstar.controller.OrderController_24162120;
import vn.iotstar.controller.ProductController_24162120;
import vn.iotstar.controller.ProfileController_24162120;
import vn.iotstar.controller.admin.CategoryController_24162120;
import vn.iotstar.controller.admin.UserController_24162120;
import vn.iotstar.controller.common.ImageServlet_24162120;
import vn.iotstar.filter.AuthFilter_24162120;
import vn.iotstar.filter.Utf8EncodingFilter_24162120;
import vn.iotstar.listener.AppBootstrapListener_24162120;

@Configuration
public class WebInfrastructureConfig_24162120 {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final long MAX_REQUEST_SIZE = 6L * 1024 * 1024;
    private static final int FILE_SIZE_THRESHOLD = 1024 * 1024;

    @Bean
    FilterRegistrationBean<Utf8EncodingFilter_24162120> utf8EncodingFilter() {
        FilterRegistrationBean<Utf8EncodingFilter_24162120> registration = new FilterRegistrationBean<>();
        registration.setFilter(new Utf8EncodingFilter_24162120());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        registration.setOrder(1);
        return registration;
    }

    @Bean
    FilterRegistrationBean<AuthFilter_24162120> authFilter() {
        FilterRegistrationBean<AuthFilter_24162120> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter_24162120());
        registration.addUrlPatterns("/admin/*");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        registration.setOrder(2);
        return registration;
    }

    @Bean
    ServletListenerRegistrationBean<AppBootstrapListener_24162120> appBootstrapListener() {
        return new ServletListenerRegistrationBean<>(new AppBootstrapListener_24162120());
    }

    @Bean
    ServletRegistrationBean<HomeController_24162120> homeControllerServlet() {
        return new ServletRegistrationBean<>(new HomeController_24162120(), "/home");
    }

    @Bean
    ServletRegistrationBean<AuthController_24162120> authControllerServlet() {
        return new ServletRegistrationBean<>(new AuthController_24162120(),
                "/login",
                "/logout",
                "/register",
                "/verify-otp",
                "/forgot-password",
                "/reset-password",
                "/resend-otp");
    }

    @Bean
    ServletRegistrationBean<ImageServlet_24162120> imageServlet() {
        return new ServletRegistrationBean<>(new ImageServlet_24162120(), "/image");
    }

    @Bean
    ServletRegistrationBean<ProductController_24162120> productControllerServlet() {
        return new ServletRegistrationBean<>(new ProductController_24162120(), "/product", "/product/detail");
    }

    @Bean
    ServletRegistrationBean<CartController_24162120> cartControllerServlet() {
        return new ServletRegistrationBean<>(new CartController_24162120(),
                "/cart", "/cart/add", "/cart/update", "/cart/remove");
    }

    @Bean
    ServletRegistrationBean<OrderController_24162120> orderControllerServlet() {
        return new ServletRegistrationBean<>(new OrderController_24162120(), "/checkout", "/orders");
    }

    @Bean
    ServletRegistrationBean<vn.iotstar.controller.admin.ProductController_24162120> adminProductControllerServlet() {
        ServletRegistrationBean<vn.iotstar.controller.admin.ProductController_24162120> registration =
                new ServletRegistrationBean<>(new vn.iotstar.controller.admin.ProductController_24162120(),
                        "/admin/products",
                        "/admin/product/add",
                        "/admin/product/insert",
                        "/admin/product/edit",
                        "/admin/product/update",
                        "/admin/product/delete");
        registration.setMultipartConfig(multipartConfig());
        return registration;
    }

    @Bean
    ServletRegistrationBean<CategoryController_24162120> categoryControllerServlet() {
        ServletRegistrationBean<CategoryController_24162120> registration =
                new ServletRegistrationBean<>(new CategoryController_24162120(),
                        "/admin/categories",
                        "/admin/category/add",
                        "/admin/category/insert",
                        "/admin/category/edit",
                        "/admin/category/update",
                        "/admin/category/delete");
        registration.setMultipartConfig(multipartConfig());
        return registration;
    }

    @Bean
    ServletRegistrationBean<ProfileController_24162120> profileControllerServlet() {
        ServletRegistrationBean<ProfileController_24162120> registration =
                new ServletRegistrationBean<>(new ProfileController_24162120(), "/profile");
        registration.setMultipartConfig(multipartConfig());
        return registration;
    }

    @Bean
    ServletRegistrationBean<UserController_24162120> userControllerServlet() {
        return new ServletRegistrationBean<>(new UserController_24162120(),
                "/admin/users",
                "/admin/user/add",
                "/admin/user/insert",
                "/admin/user/edit",
                "/admin/user/update",
                "/admin/user/delete");
    }

    @Bean
    ServletRegistrationBean<vn.iotstar.controller.VideoController_24162120> videoControllerServlet() {
        return new ServletRegistrationBean<>(new vn.iotstar.controller.VideoController_24162120(),
                "/video",
                "/video/detail");
    }

    @Bean
    ServletRegistrationBean<vn.iotstar.controller.admin.VideoController_24162120> adminVideoControllerServlet() {
        ServletRegistrationBean<vn.iotstar.controller.admin.VideoController_24162120> registration =
                new ServletRegistrationBean<>(new vn.iotstar.controller.admin.VideoController_24162120(),
                        "/admin/videos",
                        "/admin/video/add",
                        "/admin/video/insert",
                        "/admin/video/edit",
                        "/admin/video/update",
                        "/admin/video/delete");
        registration.setMultipartConfig(multipartConfig());
        return registration;
    }

    private MultipartConfigElement multipartConfig() {
        return new MultipartConfigElement("", MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
    }
}
