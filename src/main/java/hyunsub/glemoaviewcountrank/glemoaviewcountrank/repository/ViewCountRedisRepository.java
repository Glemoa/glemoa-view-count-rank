package hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ViewCountRedisRepository {
    private final StringRedisTemplate redisTemplate;

    public ViewCountRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String VIEW_COUNT_KEY = "postId::%s::viewCount";

    public Long count(Long postId) {
        String result = redisTemplate.opsForValue().get(getViewCountKey(postId));
        return result == null ? 0 : Long.parseLong(result);
    }

    public Long increase(Long postId) {
        return redisTemplate.opsForValue().increment(getViewCountKey(postId));
    }

    private String getViewCountKey(Long postId) {
        return String.format(VIEW_COUNT_KEY, postId);
    }
}
