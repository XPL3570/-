package com.confession.comm;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),


    PARAM_ERROR( 202, "参数不正确"),
    GET_OPENID_FAIL(203,"获取open异常"),
    DATA_ERROR(204, "数据异常"),
    DATA_UPDATE_ERROR(205, "数据版本异常"),

    CONTRIBUTE_OVER_LIMIT(216,"每天投稿超过限制，请稍后重试"),
    COMMENT_OVER_LIMIT(217,"每日评论超过限制，请稍后重试"),
    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    SCHOOL_REGISTERED(210, "学校已经被注册"),
    SCHOOL_NOT_SETTLED(211,"学校还未入驻"),

    LOGIN_DISABLED_ERROR(212, "该用户已被禁用"),
    REGISTER_MOBLE_ERROR(213, "手机号已被使用"),
    LOGIN_AURH(214, "需要登录"),
    LOGIN_ACL(215, "没有权限"),
    TOKEN_EXPIRE(222, "token无效或过期"),
    SUBMISSION_EXCEEDS_LIMIT(238,"放入恋爱箱超过限制，请稍后重试"),
    WITHDRAWAL_EXCEEDS_LIMIT(239,"抽纸条数量超过限制，请稍后重试"),
    NOT_OPEN_LOVE_WALL(245,"学校暂时没有开启恋爱墙，或暂时关闭！"),
    CANNOT_COMMENT(241,"您暂时被限制发言了哦，如有疑问联系表白墙"),
    CANNOT_POST(242,"您暂时被限制投稿表白了哦，如有疑问联系表白墙"),
    COMMENT_SENSITIVE_WORD_ALARM(243,"该条评论可能存在敏感词，请您调整后再次发布评论"),
    NO_SUBMISSION_DATA(244,"没有数据了哦！"), //没有投稿数据了
    ALREADY_REPORTED_IT(260,"您已经提交对该投稿的举报信息"),
    TOO_MANY_REPORTS_TODAY(261,"您今日提交的举报信息过多哦！"),
    TOO_MANY_FEEDBACK_TODAY(262,"您今日已经提交过反馈了哦，感谢您再次反馈！"),
    ABNORMAL_WALL_STATUS(270,"表白墙状态异常，请稍后重试，请联系管理员解除"),
    TOO_MANY_USER_DELETIONS(411,"当前删除操作过多，请稍后重试！"),
    UNABLE_OBTAIN_USER_WECHAT(401,"该用户不允许获取他(她)的联系方式！"),
    FREQUENT_USER_OBTAIN_WECHAT(402,"该用户被获取联系方式频繁"),
    FREQUENT_USER_ACCESS_WX(403,"获取联系方式过于频繁"),
    USER_NOT_EXIST(404,"用户不存在"),
    APPLICATION_HAS_ALREADY(405,"您已发起过申请"),
    FREQUENT_MOD_OF_USER_INFO(400,"最多一天可以修改一次用户信息哦！"),


    ;

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
