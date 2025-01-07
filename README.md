# KingOfBots-Server

**此项目为Acwing的Spingboot课程中项目的后端部分**

> [!TIP]
> 此仓库仅用于代码归档

相较于课程中的代码，此项目有以下差异：
- 一些包名不同
- 微服务无鉴权

前端仓库访问：<https://github.com/Coooolfan/KingOfBots>

# 数据库DDL

```sql
create table bot
(
    id          int auto_increment
        primary key,
    user_id     int            null,
    title       varchar(100)   null,
    description varchar(300)   null,
    content     varchar(10000) null,
    createtime  datetime       null,
    modifytime  datetime       null,
    constraint id
        unique (id)
);

create table record
(
    id         int auto_increment
        primary key,
    a_id       int           null,
    a_sx       int           null,
    a_sy       int           null,
    b_id       int           null,
    b_sx       int           null,
    b_sy       int           null,
    a_steps    varchar(1000) null,
    b_steps    varchar(1000) null,
    map        varchar(1000) null,
    loser      varchar(10)   null,
    createtime datetime      null,
    constraint id
        unique (id)
);

create table user
(
    id       int auto_increment
        primary key,
    username varchar(100)     null,
    password varchar(100)     null,
    photo    varchar(1000)    null,
    rating   int default 1500 not null,
    constraint id
        unique (id)
);
```