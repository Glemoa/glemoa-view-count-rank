package hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DailyPostRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate rankDate; // 기준 날짜 (예: 2025-11-22)

    @Column(nullable = false)
    private Long postId;        // 게시글 ID

    @Column(nullable = false)
    private Long viewCount;     // 그 날 하루 동안의 조회수

    @Column(nullable = false)
    private Integer ranking;    // 그 날의 순위 (1등, 2등...)
}
