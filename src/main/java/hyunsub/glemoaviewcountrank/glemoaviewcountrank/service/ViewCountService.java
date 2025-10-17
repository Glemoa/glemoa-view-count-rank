package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ViewCountService {
    private ViewCountRedisRepository viewCountRedisRepository;
    private ViewCountDbBackUpService viewCountDbBackUpService;

    private static final int BACK_UP_BATCH_SIZE = 100;

    public ViewCountService(ViewCountRedisRepository viewCountRedisRepository, ViewCountDbBackUpService viewCountDbBackUpService) {
        this.viewCountRedisRepository = viewCountRedisRepository;
        this.viewCountDbBackUpService = viewCountDbBackUpService;
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
}
