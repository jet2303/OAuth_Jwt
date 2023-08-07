INSERT INTO _user(`name`, `email`, `password`, `role`)
     VALUES ('testname', 'test1@naver.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'ADMIN');

INSERT INTO Board_Info(`email`, `user_name`, `title`, `content`, `board_status`, `created_date`,  `created_by`)
     VALUES('test@naver.com', 'jsan', 'title', 'content', 'REGISTERED', FORMATDATETIME(NOW(), 'yyyy-MM-dd'), 'testname');

INSERT INTO File_Info(`file_name`, `file_path`)
     VALUES('abd760b6-834f-475e-9c7e-aa159fe22399_GPU.PNG','/files/abd760b6-834f-475e-9c7e-aa159fe22399_GPU.PNG');

-- 컬럼은 백틱 ``
-- data는 작은따옴표 ''

