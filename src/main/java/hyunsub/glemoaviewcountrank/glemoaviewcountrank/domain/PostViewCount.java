package hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post_view_count")
public class PostViewCount {
    @Id
    private Long postId;
    private Long viewCount;
}
