SELECT * FROM article;
SELECT * FROM `board`;
SELECT * FROM `member`;
SELECT * FROM `genFile`;

TRUNCATE `genFile`;

# 게시물 랜덤생성 쿼리
INSERT INTO article
(regDate, updateDate, memberId, boardId, title, `body`)
SELECT NOW(), NOW(), FLOOR(RAND() * 2) + 1, FLOOR(RAND() * 2) + 1, CONCAT('제목_', FLOOR(RAND() * 1000) + 1), CONCAT('내용_', FLOOR(RAND() * 1000) + 1)
FROM article;