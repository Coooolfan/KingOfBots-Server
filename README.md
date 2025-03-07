# KingOfBots-Server <br/> Bot多语言支持：JAVA、JavaScript、Python、C++

**此项目为Acwing的Spingboot课程中项目的后端部分**

前端仓库访问：<https://github.com/Coooolfan/KingOfBots>

> [!TIP]
> 此代码仅用于概念设计与程序实现！不具备生产环境的安全性与稳定性！<br/>相关微服务的调用无鉴权！

相较于课程已有的功能，此项目有以下差异：
- 一些包名不同
- 微服务无鉴权

相较于原课程的功能，此项目有以下差异：
- 提供开箱即用的Docker镜像（仅BotRunner微服务）
- 提供Bot多语言支持（JAVA、JavaScript、Python、C++）
  - 抽象出BotRunner和BotCompiler接口
  - Java: 使用Gradle编译Jar包
  - C++: 使用g++编译可执行文件
  - JavaScript: 使用Node.js直接运行
  - Python: 使用Python解释器直接运行
- 对于需要编译的语言，提供了定时任务编译所有未编译的Bot代码
    - 无重复编译，系统会自动保留编译产物，运行时直接使用
    - 编辑Bot后会自动重新编译

# 运行指南

TODO

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