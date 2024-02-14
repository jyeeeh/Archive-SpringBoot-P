package com.sloop.archive.notice.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class NoticeDTO implements Comparable<NoticeDTO> {
    private Long id;                   // 공지사항 아이디
    private Long userId;               // 공지사항 작성자 아이디
    private String title;              // 공지사항 제목
    private String content;            // 공지사항 내용 (text)
    private int views;                 // 공지사항 조회수
    private Timestamp registerDate;    // 공지사항 등록 일시
    private Boolean pinned;            // 공지사항 상단 고정 여부. 고정 1, 기본값 0
    private Boolean updateFlag;        // 공지사항 수정 여부. 수정 1, 수정없음 0
    private Timestamp updateDate;      // 공지사항 수정 일시
    private Long updateUserId;         // 공지사항 수정자 아이디
    private Boolean deleteFlag;        // 공지사항 삭제 여부. 삭제 1, 삭제없음 0
    private Timestamp deleteDate;      // 공지사항 삭제 일시

    // 중요 여부에 따라 정렬을 위한 compareTo() 메서드 구현
    public int compareTo(NoticeDTO other) {
        Boolean thisPinned = this.pinned == null ? Boolean.FALSE : this.pinned;
        Boolean otherPinned = other.pinned == null ? Boolean.FALSE : other.pinned;

        if (thisPinned && !otherPinned) {
            return -1; // this가 중요글, other가 중요글이 아닌 경우 this를 앞으로 정렬
        } else if (!thisPinned && otherPinned) {
            return 1; // this가 중요글이 아니고, other가 중요글인 경우 other를 앞으로 정렬
        } else {
            return this.id.compareTo(other.id); // 두 글 모두 중요글이거나 중요글이 아닌 경우 ID에 따라 오름차순 정렬
        }
    }
}
