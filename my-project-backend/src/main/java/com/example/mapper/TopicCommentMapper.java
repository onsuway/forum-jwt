package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.TopicComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName TopicCommentMapper
 * @Description 帖子评论Mapper
 * @Author su
 * @Date 2024/1/21 14:53
 */
@Mapper
public interface TopicCommentMapper extends BaseMapper<TopicComment> {
}
