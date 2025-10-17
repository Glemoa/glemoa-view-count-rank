package hyunsub.glemoaviewcountrank.glemoaviewcountrank.controller;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.service.ViewCountService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostViewCountController {
    private final ViewCountService viewCountRedisService;

    public PostViewCountController(ViewCountService viewCountRedisService) {
        this.viewCountRedisService = viewCountRedisService;
    }

    @PostMapping("/{postId}/viewCount")
    public Long increaseViewCount(@PathVariable Long postId) {
        Long viewCount = viewCountRedisService.increase(postId);

        return viewCount;
    }
}
