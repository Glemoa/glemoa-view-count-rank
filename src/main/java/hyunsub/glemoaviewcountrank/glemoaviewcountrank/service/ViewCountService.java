package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.dto.RankedPostDto;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class ViewCountService {
    private final ViewCountRedisRepository viewCountRedisRepository;
    private final ViewCountDbBackUpService viewCountDbBackUpService;
    private final ReaderFeign readerFeign;

    private static final int BACK_UP_BATCH_SIZE = 100;

    public ViewCountService(ViewCountRedisRepository viewCountRedisRepository,
                            ViewCountDbBackUpService viewCountDbBackUpService, ReaderFeign readerFeign) {
        this.viewCountRedisRepository = viewCountRedisRepository;
        this.viewCountDbBackUpService = viewCountDbBackUpService;
        this.readerFeign = readerFeign;
    }

    public void increase(Long postId) {
        Long count = viewCountRedisRepository.redisIncrease(postId, LocalDate.now());

        if(count % BACK_UP_BATCH_SIZE == 0) {
            viewCountDbBackUpService.backUp(postId, count);
        }
    }

    public Long count(Long postId) {
        return viewCountRedisRepository.redisCount(postId, LocalDate.now());
    }

    public List<RankedPostDto> getDailyRank(long start, long end) {
        Set<TypedTuple<String>> dailyRank = viewCountRedisRepository.redisGetDailyRank(start, end, LocalDate.now());
        List<Long> rankedPostIds = new ArrayList<>();

        // 일간 순위가 없다면 비어 있는 채로 반환
        if(dailyRank.isEmpty()) {
            return new ArrayList<>();
        }

        for(TypedTuple<String> tuple : dailyRank) {
            String postId = tuple.getValue();
            Long viewCount = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

            // postId::302837 형식의 key를 id만 추출
            Long postIdNumber = viewCountRedisRepository.parsePostId(postId);

            if (postIdNumber != null) {
                rankedPostIds.add(postIdNumber);
                log.info("게시글 번호 : " + postIdNumber + " / 조회수 : " + viewCount);
            } else {
                log.warn("Invalid format or failed to parse: " + postId);
            }
        }

        return readerFeign.viewBookMarkedPostByPostIdList(rankedPostIds);
    }
}
