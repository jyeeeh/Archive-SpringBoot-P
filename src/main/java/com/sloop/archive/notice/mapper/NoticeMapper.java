package com.sloop.archive.notice.mapper;

import com.sloop.archive.notice.domain.NoticeDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    List<NoticeDTO> getNoticeList(@Param("start") int start, @Param("pageSize") int pageSize);
    List<NoticeDTO> getNoticesPinned();
    int getTotalCount();
    NoticeDTO getNoticeById(Long id);
    void saveNotice(NoticeDTO notice);
    void updateNotice(NoticeDTO notice);
    void deleteNotice(Long id);
    List<NoticeDTO> getNoticePinnedFirst(@Param("start") int start, @Param("pageSize") int pageSize);
    List<NoticeDTO> getAllNoticePinnedFirst(@Param("start") int start, @Param("pageSize") int pageSize);
    void increaseViews(Long id);
    int getSearchCount(@Param("keyword") String keyword);
    List<NoticeDTO> searchNotice(@Param("keyword") String keyword, @Param("start") int start, @Param("pageSize") int pageSize);
}