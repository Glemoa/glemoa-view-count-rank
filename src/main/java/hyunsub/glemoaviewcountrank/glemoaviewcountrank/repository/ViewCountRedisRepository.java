package hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class ViewCountRedisRepository {
    private final StringRedisTemplate redisTemplate;

    public ViewCountRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String DAILY_RANK_KEY = "daily_rank";
    private static final String POST_ID = "postId::%s";

    public Long count(Long postId) {
        Double score = redisTemplate.opsForZSet().score(DAILY_RANK_KEY, getViewCountKey(postId));
        return score == null ? 0L : score.longValue();
    }

    public Long increase(Long postId) {
        Double score = redisTemplate.opsForZSet().incrementScore(DAILY_RANK_KEY, getViewCountKey(postId), 1);
        return score == null ? 0L : score.longValue();
    }

    public Set<TypedTuple<String>> getDailyRank(long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(DAILY_RANK_KEY, start, end);
    }

    private String getViewCountKey(Long postId) {
        return String.format(POST_ID, postId);
    }
}
