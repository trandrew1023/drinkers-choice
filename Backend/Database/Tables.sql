use drinkersChoice;

create table user(
id int not null auto_increment,
firstname char (20) not null,
lastname char (20) not null,
username char (20) not null,
email char (30) not null,
password char (20) not null,
primary key (id));

create table post(
post_id int auto_increment,
poster char (20),
title varchar (100),
type char(20),
creation_date timestamp,
last_edit_date timestamp,
rating int,
num_comments int,
content varchar(10000),
is_deleted boolean,
valid boolean,
primary key (post_id));

create table comment(
comment_id int auto_increment,
parent_id int,
poster char(20),
rating int,
creation_date timestamp,
last_edit_date timestamp,
content varchar(1000),
is_deleted boolean,
primary key(comment_id));

create table drinker(
id int not null references user(id),
user_rank char(20));

create table driver(
driver_id int not null references user(id),
car char(50),
DOB date
);

create table image(
image_id int auto_increment,
image Blob,
primary key(image_id));	

create table drinker(
drinker_id int not null references user(id),
drinker_rank char(20),
DOB date
);

create table business(
business_id int not null auto_increment,
name char(100),
email char(50),
password char(128),
primary key(business_id)
);

create table ride(
ride_id int auto_increment,
username char (20) references user(id),
destination char (100),
location char (100),
date char (20),
value double,
accepted boolean,
primary key(ride_id)
);

insert into comment(comment_id, parent_id, poster, rating, creation_date, last_edit_date, content, is_deleted)
values(1, 1, 'bsmith', 5, '2018-10-15 12:00:00', '2018-10-15 12:00:00', 'This is a comment', false);

insert into comment(comment_id, parent_id, poster, rating, creation_date, last_edit_date, content, is_deleted)
values(null, 1, 'Trandrew', 5, '2018-10-15 12:00:00', '2018-10-15 12:00:00', 'This is a comment 2', false);

insert into comment(comment_id, parent_id, poster, rating, creation_date, last_edit_date, content, is_deleted)
values(null, 1, 'bsmith', 5, '2018-10-15 12:00:00', '2018-10-15 12:00:00', 'This is a comment 3', false);

insert into post(post_id, poster, title, type, creation_date, last_edit_date, rating, num_comments, content, is_deleted, valid)
values(1, 'Trandrew', 'First Post', 'Test', '2018-10-07 12:00:00', '2018-10-07 12:00:00', 69, 0, 'This is just a test of the first post', false, true);

insert into post(post_id, poster, title, type, creation_date, last_edit_date, rating, num_comments, content, is_deleted, valid)
values(null, 'bsmith', 'Second Post', 'Test', '2018-10-07 12:00:00', '2018-10-07 12:00:00', 69, 0, 'This is just a test of the second post', false, true);

insert into post(post_id, poster, title, type, creation_date, last_edit_date, rating, num_comments, content, is_deleted, valid)
values(null, 'Trandrew', 'Third Post', 'Test', '2018-10-07 12:00:00', '2018-10-07 12:00:00', 69, 0, 'This is just a test of the third post', false, true);

insert into post(post_id, poster, title, type, creation_date, last_edit_date, rating, num_comments, content, is_deleted, valid)
values(null, 'Trandrew', 'Fourth Post', 'Test', '2018-10-07 12:00:00', '2018-10-07 12:00:00', 69, 0, 'This is just a test of the fourth post', false, true);

insert into post(post_id, poster, title, type, creation_date, last_edit_date, rating, num_comments, content, is_deleted, valid)
values(null, 'Trandrew', 'Fifth Post', 'Test', '2018-10-07 12:00:00', '2018-10-07 12:00:00', 69, 0, 'This is just a test of the fifth post', false, true);

insert into user(id, firstname, lastname, username, password, email)
values(123456789, 'Andrew', 'Tran', 'Trandrew','password', 'atran@iastate.edu');

insert into user(id, firstname, lastname, username, password, email)
values(0, 'null', 'null', 'null','null', 'null');

SELECT * 
FROM comment 
where parent_id = 1;

