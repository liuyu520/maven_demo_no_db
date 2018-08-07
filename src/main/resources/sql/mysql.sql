create table wx_cp_command_to_suite (
	id int primary key auto_increment,
	suiteTicket varchar(126) ,
suiteId varchar(126) not null,
infoType varchar(126)  not null,
authCode varchar(126) ,
timeStamp2 varchar(126)  not null,
createTime varchar(126),
updateTime varchar(126),
remark varchar(126)
)