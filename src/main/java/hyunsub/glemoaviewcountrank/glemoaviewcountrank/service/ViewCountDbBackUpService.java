package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain.PostViewCount;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository.ViewCountDbRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ViewCountDbBackUpService {
    private final ViewCountDbRepository viewCountDbRepository;

    public ViewCountDbBackUpService(ViewCountDbRepository viewCountDbRepository) {
        this.viewCountDbRepository = viewCountDbRepository;
    }

    public void backUp(Long postId, Long viewCount) {
        viewCountDbRepository.save(PostViewCount.builder()
                .postId(postId)
                .viewCount(viewCount)
                .build());
    }
}
