package hello.mailService.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiStatisticsRepository extends JpaRepository<ApiStatistics, Long> {

    @Modifying
    @Query("update ApiStatistics a set a.count = a.count + 1 where a.serverName = :serverName")
    int updateCount(@Param("serverName") String serverName);
}
