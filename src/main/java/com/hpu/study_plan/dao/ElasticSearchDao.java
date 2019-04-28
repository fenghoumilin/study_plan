package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.ArticleES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ElasticSearchDao extends ElasticsearchRepository<ArticleES, String> {

    List<ArticleES> findByTitleContains(String title);

    List<ArticleES> findByTitleLike(String title);

    @Override
    <S extends ArticleES> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    <S extends ArticleES> S save(S s);

    @Override
    void deleteAll();
}
