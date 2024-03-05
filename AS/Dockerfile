FROM openjdk:11-jdk
WORKDIR /app

# 从当前目录复制AS文件夹的内容到容器内的/app/AS目录
COPY . /app/AS

# 在容器内的/app/AS目录下编译AS.java文件
WORKDIR /app/AS
RUN javac AS.java

# 运行AS类时，工作目录应该是/app
WORKDIR /app

# 运行AS类
CMD ["java", "AS.AS"]

