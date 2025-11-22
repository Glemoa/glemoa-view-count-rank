package hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain.DailyPostRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyPostRankRepository extends JpaRepository<DailyPostRank, Long> {
    // 특정 날짜의 랭킹 데이터를 순위 순서대로 조회
    List<DailyPostRank> findByRankDateOrderByRankingAsc(LocalDate rankDate);
}
