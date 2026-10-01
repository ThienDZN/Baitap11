package vn.iotstar.service.impl;

import java.util.List;
import vn.iotstar.dao.IVideoDao_24162120;
import vn.iotstar.dao.impl.VideoDao_24162120;
import vn.iotstar.entity.Video_24162120;
import vn.iotstar.service.IVideoService_24162120;

public class VideoServiceImpl_24162120 implements IVideoService_24162120 {
    private final IVideoDao_24162120 videoDao = new VideoDao_24162120();

    @Override
    public void insert(Video_24162120 video) {
        validate(video, true);
        videoDao.insert(video);
    }

    @Override
    public void update(Video_24162120 video) {
        validate(video, false);
        videoDao.update(video);
    }

    @Override
    public void delete(String videoId) throws Exception {
        if (videoId == null || videoId.isBlank()) throw new IllegalArgumentException("VideoId must not be empty.");
        videoDao.delete(videoId.trim());
    }

    @Override
    public Video_24162120 findById(String videoId) {
        if (videoId == null || videoId.isBlank()) return null;
        return videoDao.findById(videoId.trim());
    }

    @Override
    public List<Video_24162120> findAll() {
        return videoDao.findAll();
    }

    @Override
    public List<Video_24162120> findAllPaginated(int page, int pageSize) {
        return videoDao.findAllPaginated(Math.max(1, page), pageSize);
    }

    @Override
    public int countAll() {
        return videoDao.countAll();
    }

    @Override
    public List<Video_24162120> findActive(int page, int pageSize) {
        return videoDao.findActive(Math.max(1, page), pageSize);
    }

    @Override
    public int countActive() {
        return videoDao.countActive();
    }

    @Override
    public List<Video_24162120> findLatestActive(int limit) {
        return videoDao.findLatestActive(limit);
    }

    @Override
    public List<Video_24162120> findByCategory(int categoryId, int page, int pageSize) {
        return videoDao.findByCategory(categoryId, Math.max(1, page), pageSize);
    }

    @Override
    public int countByCategory(int categoryId) {
        return videoDao.countByCategory(categoryId);
    }

    @Override
    public List<Object[]> countGroupByCategory() {
        return videoDao.countGroupByCategory();
    }

    private void validate(Video_24162120 video, boolean isInsert) {
        if (video == null) throw new IllegalArgumentException("Video_24162120 data is not valid.");
        if (video.getVideoId() == null || video.getVideoId().isBlank())
            throw new IllegalArgumentException("VideoId must not be empty.");
        if (video.getVideoId().trim().length() > 50)
            throw new IllegalArgumentException("VideoId must not exceed 50 characters.");
        video.setVideoId(video.getVideoId().trim());
        if (video.getTitle() == null || video.getTitle().isBlank())
            throw new IllegalArgumentException("Title must not be empty.");
        video.setTitle(video.getTitle().trim());
        if (video.getCategory() == null)
            throw new IllegalArgumentException("Please choose a category.");
        if (video.getViews() < 0)
            throw new IllegalArgumentException("Views must be >= 0.");
        if (video.getPoster() != null) video.setPoster(video.getPoster().trim());
        if (video.getDescription() != null) video.setDescription(video.getDescription().trim());
        if (isInsert && videoDao.findById(video.getVideoId()) != null)
            throw new IllegalArgumentException("VideoId already exists.");
    }
}
