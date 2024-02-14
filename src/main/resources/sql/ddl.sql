USE archive;

-- DROP TABLE IF EXISTS `tb_user_log`;
-- DROP TABLE IF EXISTS `tb_recommendation_content`;
-- DROP TABLE IF EXISTS `tb_recommendation`;
-- DROP TABLE IF EXISTS `tb_keyword_recommendation`;
-- DROP TABLE IF EXISTS `tb_user_role`;
-- DROP TABLE IF EXISTS `tb_post_log`;
-- DROP TABLE IF EXISTS `tb_download_log`;
-- DROP TABLE IF EXISTS `tb_content_category`;
-- DROP TABLE IF EXISTS `tb_notice`;
-- DROP TABLE IF EXISTS `tb_access_log`;
-- DROP TABLE IF EXISTS `tb_category`;
-- DROP TABLE IF EXISTS `tb_image`;
-- DROP TABLE IF EXISTS `tb_content`;
-- DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
                           `id`	bigint auto_increment	NOT NULL,
                           `userid`	varchar(50)	NOT NULL,
                           `password`	varchar(255)	NOT NULL,
                           `name`	varchar(20)	NOT NULL,
                           `email`	varchar(100)	NOT NULL,
                           `phone`	varchar(20)	NULL,
                           `register_date`	timestamp	NOT NULL	DEFAULT now(),
                           `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                           `update_date`	timestamp	NULL	DEFAULT null,
                           `delete_flag`	tinyint	NULL	DEFAULT 0	COMMENT '삭제 1, 삭제없음 0',
                           `delete_date`	timestamp	NULL	DEFAULT null,
                           CONSTRAINT `PK_TB_USER` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_content` (
                              `id`	bigint auto_increment	NOT NULL,
                              `user_id`	bigint	NOT NULL,
                              `type`	tinyint	NOT NULL	DEFAULT 1	COMMENT '일러스트 1, 아이콘 2, 사진 3, 캐릭터 4',
                              `charge`	tinyint	NOT NULL	DEFAULT 0	COMMENT '유료 1, 무료 0',
                              `view`	tinyint	NOT NULL	DEFAULT 1	COMMENT '노출1, 비노출0 (메인페이지기준)',
                              `title`	varchar(255)	NOT NULL,
                              `keyword`	varchar(255)	NULL,
                              `description`	varchar(255)	NULL,
                              `register_date`	timestamp	NOT NULL	DEFAULT now(),
                              `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                              `update_date`	timestamp	NULL,
                              `delete_flag`	tinyint	NULL	DEFAULT 0	COMMENT '삭제 1, 삭제없음 0',
                              `delete_date`	timestamp	NULL,
                              `approve_flag`	tinyint	NULL	DEFAULT 0	COMMENT '승인 1, 승인대기 0, 승인거절 2',
                              `approve_user_id`	bigint	NULL,
                              CONSTRAINT `PK_TB_CONTENT` PRIMARY KEY (`id`)
);


CREATE TABLE `tb_image` (
                            `id`	bigint auto_increment	NOT NULL,
                            `content_id`	bigint	NOT NULL,
                            `extension`	varchar(255)	NOT NULL,
                            `original_name`	varchar(255)	NOT NULL,
                            `saved_name`	varchar(255)	NOT NULL,
                            `thumb_name`	varchar(255)	NOT NULL,
                            `original_path`	varchar(255)	NOT NULL,
                            `saved_path`	varchar(255)	NOT NULL,
                            `thumb_path`	varchar(255)	NOT NULL,
                            CONSTRAINT `PK_TB_IMAGE` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_category` (
                               `id`	bigint auto_increment	NOT NULL	COMMENT '카테고리 아이디',
                               `parent_id`	bigint	NULL	COMMENT '상위 카테고리 아이디',
                               `name`	varchar(255)	NOT NULL	COMMENT '카테고리 이름',
                               `show_flag`	tinyint	NOT NULL	DEFAULT 1	COMMENT '사용 1, 미사용 0',
                               `depth`	int	NOT NULL	DEFAULT 1	COMMENT '깊이',
                               `orders`	int	NOT NULL	DEFAULT 1	COMMENT '같은 깊이 내 순서',
                               `register_date`	timestamp	NULL	COMMENT '등록일시',
                               `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                               `update_date`	timestamp	NULL	COMMENT '수정일시',
                               `user_id`	bigint	NULL	COMMENT '마지막 수정한 회원 아이디',
                               CONSTRAINT `PK_TB_CATEGORY` PRIMARY KEY (`id`),
                               Foreign Key (parent_id) REFERENCES tb_category(id) on delete cascade on update cascade
);

CREATE TABLE `tb_access_log` (
                                 `id`	bigint auto_increment	NOT NULL,
                                 `user_id`	bigint	NOT NULL,
                                 `login_flag`	tinyint	NULL	DEFAULT 1	COMMENT '성공 1, 실패 0',
                                 `log_timestamp`	timestamp	NOT NULL,
                                 CONSTRAINT `PK_TB_ACCESS_LOG` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_notice` (
                             `id`	bigint auto_increment	NOT NULL,
                             `user_id`	bigint	NOT NULL,
                             `title`	varchar(255)	NOT NULL,
                             `content`	text	NOT NULL,
                             `views`	int	NOT NULL	DEFAULT 0,
                             `register_date`	timestamp	NOT NULL	DEFAULT now(),
                             `pinned`	tinyint	NULL	DEFAULT 0	COMMENT '고정 1, 기본값 0',
                             `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                             `update_date`	timestamp	NULL	DEFAULT null,
                             `update_user_id`	bigint	NULL,
                             `delete_flag`	tinyint	NULL	DEFAULT 0	COMMENT '삭제 1, 삭제없음 0',
                             `delete_date`	datetime	NULL	DEFAULT null,
                             CONSTRAINT `PK_TB_NOTICE` PRIMARY KEY (`id`)
);


CREATE TABLE `tb_content_category` (
                                       `id`	bigint auto_increment	NOT NULL	COMMENT '콘텐츠_카테고리 아이디',
                                       `category_id`	bigint	NOT NULL	COMMENT '카테고리 아이디',
                                       `content_id`	bigint	NOT NULL	COMMENT '콘텐츠 아이디',
                                       CONSTRAINT `PK_TB_CONTENT_CATEGORY` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_download_log` (
                                   `id`	bigint auto_increment	NOT NULL,
                                   `user_id`	bigint	NOT NULL,
                                   `content_id`	bigint	NOT NULL,
                                   `log_timestamp`	timestamp	NOT NULL,
                                   CONSTRAINT `PK_TB_DOWNLOAD_LOG` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_post_log` (
                               `id`	bigint auto_increment	NOT NULL,
                               `user_id`	bigint	NOT NULL,
                               `content_id`	bigint	NOT NULL,
                               `log_timestamp`	timestamp	NOT NULL,
                               CONSTRAINT `PK_TB_POST_LOG` PRIMARY KEY (`id`)
);


CREATE TABLE `tb_user_role` (
                                `id`	bigint auto_increment	NOT NULL,
                                `user_id`	bigint	NOT NULL,
                                `role`	varchar(30)	NOT NULL	DEFAULT 'ROLE_USER'	COMMENT 'ROLE_STAFF, ROLE_ADMIN, DISABLED',
                                CONSTRAINT `PK_TB_USER_ROLE` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_keyword_recommendation` (
                                             `id`	bigint auto_increment	NOT NULL	COMMENT '추천 키워드 아이디',
                                             `keyword`	varchar(255)	NOT NULL	COMMENT '키워드 내용',
                                             `orders`	int	NOT NULL	COMMENT '메인 노출 순서',
                                             `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                                             `update_date`	timestamp	NULL	COMMENT '수정일시',
                                             `user_id`	bigint NULL	COMMENT '수정한 회원 아이디',
                                             `show_flag`	tinyint	NULL	DEFAULT 1	COMMENT '사용 1, 미사용 0',
                                             CONSTRAINT `PK_TB_KEYWORD_RECOMMENDATION` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_recommendation` (
                                     `id`	bigint auto_increment	NOT NULL	COMMENT '추천 큐레이팅 아이디',
                                     `search_keyword`	varchar(255)	NOT NULL	COMMENT '추천 문구의 전체보기 선택 시, 검색될 키워드. (예) 겨울, 새해',
                                     `orders`	int	NOT NULL	COMMENT '메인 노출 순서',
                                     `update_flag`	tinyint	NULL	DEFAULT 0	COMMENT '수정 1, 수정없음 0',
                                     `update_date`	timestamp	NULL	COMMENT '수정일시',
                                     `show_flag`	tinyint	NULL	DEFAULT 1	COMMENT '사용 1, 미사용 0',
                                     CONSTRAINT `PK_TB_RECOMMENDATION` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_recommendation_content` (
                                             `id`	bigint auto_increment	NOT NULL	COMMENT '콘텐츠_추천큐레이팅 아이디',
                                             `user_id`	bigint NULL	COMMENT '승인한 회원 아이디',
                                             `content_id`	bigint	NOT NULL	COMMENT '콘텐츠 아이디',
                                             `recommendation_id`	bigint	NOT NULL	COMMENT '추천 큐레이팅 아이디',
                                             CONSTRAINT `PK_TB_RECOMMENDATION_CONTENT` PRIMARY KEY (`id`)
);

CREATE TABLE `tb_user_log` (
                               `user_id`	bigint	NOT NULL,
                               `download_count`	bigint	NOT NULL	DEFAULT 0,
                               `approve_count`	bigint	NOT NULL	DEFAULT 0,
                               `latest_login`	timestamp	NULL	DEFAULT null,
                               CONSTRAINT `PK_TB_USER_LOG` PRIMARY KEY (`user_id`)
);

ALTER TABLE `tb_image` ADD CONSTRAINT `FK_tb_content_TO_tb_image_1` FOREIGN KEY (
                                                                                 `content_id`
    )
    REFERENCES `tb_content` (
                             `id`
        );


ALTER TABLE `tb_access_log` ADD CONSTRAINT `FK_tb_user_TO_tb_access_log_1` FOREIGN KEY (
                                                                                        `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_notice` ADD CONSTRAINT `FK_tb_user_TO_tb_notice_1` FOREIGN KEY (
                                                                                `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_notice` ADD CONSTRAINT `FK_tb_user_TO_tb_notice_2` FOREIGN KEY (
                                                                                `update_user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_content` ADD CONSTRAINT `FK_tb_user_TO_tb_content_1` FOREIGN KEY (
                                                                                  `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_content` ADD CONSTRAINT `FK_tb_user_TO_tb_content_2` FOREIGN KEY (
                                                                                  `approve_user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_content_category` ADD CONSTRAINT `FK_tb_category_TO_tb_content_category_1` FOREIGN KEY (
                                                                                                        `category_id`
    )
    REFERENCES `tb_category` (
                              `id`
        );

ALTER TABLE `tb_content_category` ADD CONSTRAINT `FK_tb_content_TO_tb_content_category_1` FOREIGN KEY (
                                                                                                       `content_id`
    )
    REFERENCES `tb_content` (
                             `id`
        );

ALTER TABLE `tb_download_log` ADD CONSTRAINT `FK_tb_user_TO_tb_download_log_1` FOREIGN KEY (
                                                                                            `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_download_log` ADD CONSTRAINT `FK_tb_content_TO_tb_download_log_1` FOREIGN KEY (
                                                                                               `content_id`
    )
    REFERENCES `tb_content` (
                             `id`
        );

ALTER TABLE `tb_post_log` ADD CONSTRAINT `FK_tb_user_TO_tb_post_log_1` FOREIGN KEY (
                                                                                    `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_post_log` ADD CONSTRAINT `FK_tb_content_TO_tb_post_log_1` FOREIGN KEY (
                                                                                       `content_id`
    )
    REFERENCES `tb_content` (
                             `id`
        );

ALTER TABLE `tb_user_role` ADD CONSTRAINT `FK_tb_user_TO_tb_user_role_1` FOREIGN KEY (
                                                                                      `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_keyword_recommendation` ADD CONSTRAINT `FK_tb_user_TO_tb_keyword_recommendation_1` FOREIGN KEY (
                                                                                                                `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_recommendation_content` ADD CONSTRAINT `FK_tb_user_TO_tb_recommendation_content_1` FOREIGN KEY (
                                                                                                                `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );

ALTER TABLE `tb_recommendation_content` ADD CONSTRAINT `FK_tb_content_TO_tb_recommendation_content_1` FOREIGN KEY (
                                                                                                                   `content_id`
    )
    REFERENCES `tb_content` (
                             `id`
        );

ALTER TABLE `tb_recommendation_content` ADD CONSTRAINT `FK_tb_recommendation_TO_tb_recommendation_content_1` FOREIGN KEY (
                                                                                                                          `recommendation_id`
    )
    REFERENCES `tb_recommendation` (
                                    `id`
        );

ALTER TABLE `tb_user_log` ADD CONSTRAINT `FK_tb_user_TO_tb_user_log_1` FOREIGN KEY (
                                                                                    `user_id`
    )
    REFERENCES `tb_user` (
                          `id`
        );