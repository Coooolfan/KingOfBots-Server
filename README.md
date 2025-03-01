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
CREATE TABLE bot
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NULL,
    title       VARCHAR(100) CHARACTER SET utf8mb4 NULL,
    description VARCHAR(300) CHARACTER SET utf8mb4 NULL,
    content     VARCHAR(10000) CHARACTER SET utf8mb4 NULL,
    language    VARCHAR(100) CHARACTER SET utf8mb4 NULL,
    status      VARCHAR(100) NULL,
    target_file varchar(200) NULL,
    createtime  DATETIME NULL,
    modifytime  DATETIME NULL,
    CONSTRAINT id UNIQUE (id)
) CHARACTER SET utf8mb4;

CREATE TABLE record
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    a_id       INT NULL,
    a_sx       INT NULL,
    a_sy       INT NULL,
    b_id       INT NULL,
    b_sx       INT NULL,
    b_sy       INT NULL,
    a_steps    VARCHAR(1000) CHARACTER SET utf8mb4 NULL,
    b_steps    VARCHAR(1000) CHARACTER SET utf8mb4 NULL,
    map        VARCHAR(1000) CHARACTER SET utf8mb4 NULL,
    loser      VARCHAR(10) CHARACTER SET utf8mb4 NULL,
    createtime DATETIME NULL,
    CONSTRAINT id UNIQUE (id)
) CHARACTER SET utf8mb4;

CREATE TABLE user
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) CHARACTER SET utf8mb4 NULL,
    password VARCHAR(100) CHARACTER SET utf8mb4 NULL,
    photo    VARCHAR(1000) CHARACTER SET utf8mb4 NULL,
    rating   INT DEFAULT 1500 NOT NULL,
    CONSTRAINT id UNIQUE (id)
) CHARACTER SET utf8mb4;
```