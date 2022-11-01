package com.tencent.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.tencent.yygh.user.mapper")
public class UserInfoConfig {
}
