// ============================================================
// 旧物改造教程分享平台 — 后端 Spring Boot 启动类
//
// 技术栈：Spring Boot 2.x + MyBatis-Plus + JdbcTemplate + MySQL
// 端口：8888
// ============================================================
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目入口
 * 
 * 系统分为两套后端体系：
 * 1. 旧系统（CRUD 自动生成的代码）：
 *    - 实体类：Yonghuxinxi, Ershouwupin, Dingdan, Collect, Liuyanban 等
 *    - 控制器：YonghuxinxiController, ErshouwupinController, FrontController 等
 *    - 使用 MyBatis-Plus 的 Service/DAO 层
 *    - 后端管理系统（houtai 中 yonghu 角色）仍然使用这些接口
 * 
 * 2. 新系统（教程平台专用）：
 *    - TutorialPlatformController（/front/v2/*）
 *    - 直接使用 JdbcTemplate 操作 tp_* 前缀的数据表
 *    - 为 qiantai（前台）和 houtai（管理员）提供 RESTful API
 *    - 覆盖：教程管理、社区论坛、材料包交易、用户管理、管理后台
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("========================================");
        System.out.println("  旧物改造教程分享平台 后端启动成功！");
        System.out.println("  接口地址: http://localhost:8888");
        System.out.println("========================================");
    }

}
