package hyunsub.glemoaviewcountrank.glemoaviewcountrank.repository;

import hyunsub.glemoaviewcountrank.glemoaviewcountrank.domain.PostViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewCountDbRepository extends JpaRepository<PostViewCount, Long> {
    @Query(
            value = "update post_view_count " +
                    "set view_count = :viewCount " +
                    "where post_id = :postId and view_count < :viewCount",
            nativeQuery = true
    )
    @Modifying
    int updateViewCount(
            @Param("postId") Long postId,
            @Param("viewCount") Long viewCount
    );

    @Modifying
    @Query(
            value = "INSERT INTO post_view_count (post_id, view_count) " +
                    "VALUES (:postId, :dailyCount) " +
                    "ON DUPLICATE KEY UPDATE view_count = view_count + :dailyCount",
            nativeQuery = true
    )
    void addViewCount(
            @Param("postId") Long postId,
            @Param("dailyCount") Long dailyCount
    );
}
