package hyunsub.glemoaviewcountrank.glemoaviewcountrank.controller;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.dto.RankedPostDto;
import hyunsub.glemoaviewcountrank.glemoaviewcountrank.service.ViewCountService;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class PostViewCountController {
    private final ViewCountService viewCountService;

    public PostViewCountController(ViewCountService viewCountService) {
        this.viewCountService = viewCountService;
    }

    // 조회수 1 증가 API
    @PostMapping("/views/{postId}")
    public Long increaseViewCount(@PathVariable Long postId) {
        return viewCountService.increase(postId);
    }

    // 특정 게시물 조회수 조회 API
    @GetMapping("/views/{postId}")
    public Long getViewCount(@PathVariable Long postId) {
        return viewCountService.count(postId);
    }

    // 일간 순위 조회 API (상위 10개)
    @GetMapping("/ranks/daily")
    public List<RankedPostDto> getDailyRank() {
        return viewCountService.getDailyRank(0, 9);
    }
}
