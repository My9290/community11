package com.nowcoder.community1.community1.dao.elasticsearch;

import com.nowcoder.community1.community1.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {


//    Page<DiscussPost> search(SearchQuery searchQuery);
}
