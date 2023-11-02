package com.confession.comm;

public class RedisConstant {
    // 用户相关
    public static final String USER_PREFIX = "user:";

    public static final String USER_DTO_PREFIX = "userDTO:";

    public static final String USER_POSTED_NODE= "userPostedNote:";//用户放入的纸条

    public static final String USER_OBTAINED_NODE="userObtainedNote";//用户抽到的纸条


    public static final String SCHOOL_INDEX_INFO= "schoolIndexInfo:";   //学校首页信息


    //学校下面的表白墙信息 +id，加载学校下面的时候用一下
    public static final String WALL_UNDER_SCHOOL= "wallUnderSchool:";

    // 表白墙投稿set集合 后面+墙id
    public static final String WALL_POSTS_PREFIX = "wall_records:";

    public static final String  SCHOOL_WALL_MAIN_LIST_MOD_LOCK=" school_wall_main_list_modification_lock:"; //学校表白墙修改锁

    public static final String CONFESSION_PREFIX_LOCK="confession_record_lock:"; //记录锁 锁的是修改单个投稿记录 加载改投稿的时候加锁？

    public static final String POST_SUBMISSION_RECORD ="wall_submission_record:";  //发布记录+投稿id



    public static final String  NUMBER_USER_DELETIONS="number_user_deletions:";//用户删除操作count记录+用户id

    //用户已经发布投稿缓存 后面+用户id
    public static final String USER_PUBLISHED_POSTS="userPublishedPosts:";

    //用户投稿还未审核的缓存 后面+用户id
    public static final String USER_PENDING_POSTS="userPendingPosts:";

    // 评论相关
    public static final String USER_COMMENT_REPLY="user_comment_reply:";  //用户评论回复+用户id +:分页参数
    public static final String COMMENT_PREFIX = "comment:";
    //学校抽取纸条锁
    public static final String SCHOOL_EXTRACTION_LOCK="school_extraction_lock:";

    //用户添加好友缓存    + 用户id  我加别人
    public static final String USER_ADD_FRIENDS="user_add_friend:";

    //用户好友申请缓存    +用户id   别人加我
    public static final String USER_FRIEND_APPLICATION="user_friend_application:";


}

