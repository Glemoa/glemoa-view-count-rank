package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.dto.RankedPostDto;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long increase(Long postId) {
        Long count = viewCountRedisRepository.increase(postId);

        if(count % BACK_UP_BATCH_SIZE == 0) {
            viewCountDbBackUpService.backUp(postId, count);
        }

        return count;
    }

    public Long count(Long postId) {
        return viewCountRedisRepository.count(postId);
    }

    public List<RankedPostDto> getDailyRank(long start, long end) {
        Set<TypedTuple<String>> dailyRank = viewCountRedisRepository.getDailyRank(start, end);
        List<Long> rankedPostIds = new ArrayList<>();

        for(TypedTuple<String> tuple : dailyRank) {
            Long postIdNumber = null;
            String postId = tuple.getValue();
            Long viewCount = tuple.getScore().longValue();

            // "::"를 기준으로 문자열을 분리합니다.
            String[] parts = postId.split("::");

            // 배열의 두 번째 요소 (인덱스 1)가 숫자 부분입니다.
            if (parts.length > 1) {
                postIdNumber = Long.parseLong(parts[1]); // 결과: "1", "2" 등
                rankedPostIds.add(postIdNumber);
            } else {
                // 구분자가 없는 경우 처리 로직 (선택 사항)
                log.info("Invalid format: " + postId);
            }
            log.info("게시글 번호 : " + postIdNumber + " / 조회수 : " + viewCount);
        }

        List<RankedPostDto> rankedPostDtos = readerFeign.viewBookMarkedPostByPostIdList(rankedPostIds);

        return rankedPostDtos;
    }
}
