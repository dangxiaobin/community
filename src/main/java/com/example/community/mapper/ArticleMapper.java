package com.example.community.mapper;

import com.example.community.entity.Article;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper {

    @Insert("insert into article(title,description,tag,author_id,create_time,modified_time) values (#{title},#{description},#{tag},#{authorId},#{createTime},#{modifiedTime})")
    int addArticle(Article article);
}
