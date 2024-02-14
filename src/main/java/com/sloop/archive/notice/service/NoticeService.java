package com.sloop.archive.notice.service;

import com.sloop.archive.notice.domain.NoticeDTO;
import com.sloop.archive.notice.mapper.NoticeMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class NoticeService {

    private final NoticeMapper noticeMapper;

    @Autowired
    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    public List<NoticeDTO> getNoticeList(int start, int pageSize) {
        List<NoticeDTO> pinnedNotices = noticeMapper.getNoticesPinned();
        List<NoticeDTO> notices = noticeMapper.getNoticeList(start, pageSize);
        notices.removeAll(pinnedNotices);
        pinnedNotices.addAll(notices);
        return pinnedNotices;
    }

    public List<NoticeDTO> getAllNoticePinnedFirst(int start, int pageSize) {
        List<NoticeDTO> noticeList = noticeMapper.getAllNoticePinnedFirst(start, pageSize);
        Collections.sort(noticeList); // 중요 여부에 따라 정렬
        return noticeList;
    }

    public List<NoticeDTO> getNoticePinnedFirst(int start, int pageSize) {
        List<NoticeDTO> noticeList = noticeMapper.getNoticePinnedFirst(start, pageSize);
        Collections.sort(noticeList); // 중요 여부에 따라 정렬
        return noticeList;
    }

    public int getTotalCount() {
        return noticeMapper.getTotalCount();
    }
    public NoticeDTO getNoticeById(Long id) {
        return noticeMapper.getNoticeById(id);
    }

    public void saveNotice(NoticeDTO notice) throws Exception {
        if (notice.getTitle() == null || notice.getTitle().trim().equals("") ||
                notice.getContent() == null || notice.getContent().trim().equals("")) {
            throw new Exception("제목과 내용은 모두 작성해야 합니다.");
        }

        noticeMapper.saveNotice(notice);
    }

    public void updateNotice(NoticeDTO notice) throws Exception {
        if (notice.getTitle() == null || notice.getTitle().trim().equals("") ||
                notice.getContent() == null || notice.getContent().trim().equals("")) {
            throw new Exception("제목과 내용은 모두 작성해야 합니다.");
        }

        noticeMapper.updateNotice(notice);
    }

    public void deleteNotice(Long id) {
        noticeMapper.deleteNotice(id);
    }

    public int getSearchCount(String keyword) {
        return noticeMapper.getSearchCount(keyword);
    }

    public List<NoticeDTO> searchNotice(String keyword, int start, int pageSize) {
        log.info("keyword" + keyword + "  start" + start + "  ps" + pageSize);
        return noticeMapper.searchNotice(keyword, start, pageSize);
    }

    @Transactional
    public NoticeDTO getNoticeAndUpdateViews(Long id) {
        noticeMapper.increaseViews(id);
        return noticeMapper.getNoticeById(id);
    }

}
