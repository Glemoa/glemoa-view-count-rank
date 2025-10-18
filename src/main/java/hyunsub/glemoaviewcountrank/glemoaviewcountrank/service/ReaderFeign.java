package hyunsub.glemoaviewcountrank.glemoaviewcountrank.service;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.dto.RankedPostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "glemoa-reader")
public interface ReaderFeign {

    @PostMapping("/post/viewBookMarkedPost")
    List<RankedPostDto> viewBookMarkedPostByPostIdList(List<Long> postIdList);
}
