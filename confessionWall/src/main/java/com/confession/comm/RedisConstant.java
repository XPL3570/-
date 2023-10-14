package com.confession.comm;

public class RedisConstant {
    // 用户相关
    public static final String USER_PREFIX = "user:";

    public static final String USER_DTO_PREFIX = "userDTO:";

    public static final String USER_POSTED_NODE= "userPostedNote:";//用户放入的纸条

    public static final String USER_OBTAINED_NODE="userObtainedNote";//用户抽到的纸条


    public static final String SCHOOL_INDEX_INFO= "schoolIndexInfo:";   //学校首页信息


    //学校下面的表白墙信息 +id
    public static final String WALL_UNDER_SCHOOL= "wallUnderSchool:";

    // 表白墙投稿记录 后面+id
    public static final String CONFESSION_PREFIX = "confession_record:";

    public static final String  SCHOOL_WALL_MAIN_LIST_MOD_LOCK=" school_wall_main_list_modification_lock:";

    public static final String CONFESSION_PREFIX_LOCK="confession_record_lock:";

    public static final String WALL_SUBMISSION_RECORD="wall_submission_record";  //发布记录+id

    //用户已经发布投稿缓存 后面+用户id
    public static final String USER_PUBLISHED_POSTS="userPublishedPosts:";

    //用户投稿还未审核的缓存 后面+用户id
    public static final String USER_PENDING_POSTS="userPendingPosts:";

    // 评论相关
    public static final String USER_COMMENT_REPLY="user_comment_reply";  //用户评论回复
    public static final String COMMENT_PREFIX = "comment:";

    //学校抽取纸条锁
    public static final String SCHOOL_EXTRACTION_LOCK="school_extraction_lock:";

}

