-- insert into user (`id`,`email`, `email_verified`, `image_url`, `name`, `password`, `provider`, `provider_id`, `role`, `useyn`) 
--     values (1,'test@naver.com', true, 'test image_url', 'test', 'password', 'local', 'test provider id', 'USER', 1);

INSERT INTO _user (`id`,`email`,`name`)
    VALUES (1,'test1@naver.com','test1');

INSERT INTO _user (`id`,`email`,`name`)
VALUES (2,'test2@naver.com','test2');

INSERT INTO _user (`id`,`email`)
    VALUES (3,'test3@naver.com');
