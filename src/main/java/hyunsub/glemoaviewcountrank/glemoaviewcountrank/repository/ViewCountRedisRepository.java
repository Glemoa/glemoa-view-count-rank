package hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class ViewCountRedisRepository {
    private final StringRedisTemplate redisTemplate;

    public ViewCountRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String DAILY_RANK_KEY_FORMAT = "daily_rank::%s";
    private static final String POST_ID_FORMAT = "postId::%s";

    /**
     * 특정 날짜의 특정 게시글 조회수 조회
     */
    public Long redisCount(Long postId, LocalDate date) {
        Double score = redisTemplate.opsForZSet().score(getDayKey(date), getPostViewCountKey(postId));
        return score == null ? 0L : score.longValue();
    }

    /**
     * 특정 날짜의 특정 게시글 조회수 증가
     */
    public Long redisIncrease(Long postId, LocalDate date) {
        Double score = redisTemplate.opsForZSet().incrementScore(getDayKey(date), getPostViewCountKey(postId), 1);

        // 데이터 수명 연장 (오늘 기준 D+1일)
        redisTemplate.expire(getDayKey(date), 1, TimeUnit.DAYS);

        return score == null ? 0L : score.longValue();
    }

    /**
     * 특정 날짜의 랭킹 조회 (범위 지정)
     */
    public Set<TypedTuple<String>> redisGetDailyRank(long start, long end, LocalDate date) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(getDayKey(date), start, end);
    }

    /**
     * 특정 날짜의 Key 삭제 (스케줄러 정리용)
     */
    public void deleteKey(LocalDate date) {
        redisTemplate.delete(getDayKey(date));
    }

    private String getDayKey(LocalDate date) {
        String day = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format(DAILY_RANK_KEY_FORMAT, day);
    }

    private String getPostViewCountKey(Long postId) {
        return String.format(POST_ID_FORMAT, postId);
    }

    public Long parsePostId(String postValue) {
        try {
            return Long.parseLong(postValue.split("::")[1]);
        } catch (Exception e) {
            return null; // 파싱 실패 시 처리
        }
    }
}
