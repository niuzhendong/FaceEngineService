package com.niuzhendong.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "face")
public class FaceProp {
    private String appId;
    private String sdkKey;
    private String activeKey;
    private String activeFile;
}
