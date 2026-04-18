package com.hoanggiang.order_service.config;

import com.hoanggiang.order_service.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class InventoryClientConfig {
    @Value("${inventory.url}")
    private String inventoryUrl;

    @Bean
    public InventoryClient inventoryClient() {
        // 1. Khởi tạo một RESTClient với BASEURL và cấu hình Timeout
        RestClient restClient = RestClient.builder()
                                          .baseUrl(this.inventoryUrl)
                                          .requestFactory(getClientHttpRequestFactory())
                                          .build();
        // 2. Chuyển RESTClient thành Adapter để dùng HTTP Interface
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        // 3. Sử dụng Proxy Factory để thực thi Interface
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                                                                 .build();
        // 3. Trả về Bean InventoryClient để dụng cho service
        return factory.createClient(InventoryClient.class);
    }

    // Cấu hình thời gian chờ
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                                                                                    .withConnectTimeout(Duration.ofSeconds(3)) // Thời gian tối đa để thiết lập kết nối mạng
                                                                                    .withReadTimeout(Duration.ofSeconds(3));   // Thời gian tối đa chờ Service đích phản hồi dữ liệu
        return ClientHttpRequestFactoryBuilder.detect()
                                              .build(settings);
    }
}
