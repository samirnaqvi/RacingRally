CREATE DATABASE RALLYRACING;
create TABLE  user(
id int PRIMARY KEY AUTO_INCREMENT,
NAME varchar(32),
highestscore INT );
insert into user values(null,'admin',-1);
insert into user values(null,'MaxTheRacer',25000);
