package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain.DailyPostRank;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.DailyPostRankRepository;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountDbRepository;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRankScheduler {

    private final ViewCountDbRepository viewCountDbRepository;
    private final ViewCountRedisRepository viewCountRedisRepository;
    private final DailyPostRankRepository dailyPostRankRepository;

    // 매일 새벽 0시 10분에 실행 (초 분 시 일 월 요일)
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void migrateYesterdayData() {
        // 1. 어제 날짜 구하기 (오늘이 23일이면 22일 데이터를 처리)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("=== Daily Rank Migration Started for {} ===", yesterday);

        // 2. Redis에서 어제자 모든 랭킹 데이터 가져오기 (0 ~ -1: 전체 조회)
        Set<TypedTuple<String>> ranks = viewCountRedisRepository.redisGetDailyRank(0, -1, yesterday);

        if (ranks == null || ranks.isEmpty()) {
            log.info("No ranking data found for {}", yesterday);
            return;
        }

        int currentRank = 1;
        for (TypedTuple<String> tuple : ranks) {
            String member = tuple.getValue();
            Long dailyViewCount = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

            // 3. 멤버 문자열에서 PostId 파싱 (Repository에 위임)
            Long postId = viewCountRedisRepository.parsePostId(member);

            if (postId != null && dailyViewCount != null) {
                // 4. DB에 히스토리 저장 (DailyPostRank 엔티티)
                DailyPostRank history = DailyPostRank.builder()
                        .rankDate(yesterday)
                        .postId(postId)
                        .viewCount(dailyViewCount.longValue())
                        .ranking(currentRank++) // 1등부터 차례대로 순위 매김
                        .build();

                dailyPostRankRepository.save(history);

                viewCountDbRepository.addViewCount(postId, dailyViewCount);
            }
        }

        log.info("=== Migration Finished. Total items: {} ===", ranks.size());

        // 이관 후 Redis 데이터를 즉시 삭제
         viewCountRedisRepository.deleteKey(yesterday);
    }
}