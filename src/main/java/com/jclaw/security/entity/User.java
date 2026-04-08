package com.jclaw.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * 用户实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("\"user\"")
public class User {
    
    /** 用户 ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /** 用户名 */
    private String username;
    
    /** 邮箱 */
    private String email;
    
    /** 密码（加密存储） */
    private String password;
    
    /** 手机号 */
    private String phone;
    
    /** 用户状态：active, inactive, banned */
    private String status;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
