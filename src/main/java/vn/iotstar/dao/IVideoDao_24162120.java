package vn.iotstar.dao;

import java.util.List;
import vn.iotstar.entity.Video_24162120;

public interface IVideoDao_24162120 {
    void insert(Video_24162120 video);
    void update(Video_24162120 video);
    void delete(String videoId) throws Exception;
    Video_24162120 findById(String videoId);
    List<Video_24162120> findAll();
    List<Video_24162120> findAllPaginated(int page, int pageSize);
    int countAll();
    List<Video_24162120> findActive(int page, int pageSize);
    int countActive();
    List<Video_24162120> findLatestActive(int limit);
    List<Video_24162120> findByCategory(int categoryId, int page, int pageSize);
    int countByCategory(int categoryId);
    List<Object[]> countGroupByCategory();
}
