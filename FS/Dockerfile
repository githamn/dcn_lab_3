FROM openjdk:11-jdk
WORKDIR /app

# 从当前目录复制FS文件夹的内容到容器内的/app/FS目录
COPY . /app/FS

# 在容器内的/app/FS目录下编译FS.java文件
WORKDIR /app/FS
RUN javac FS.java

# 运行FS类时，工作目录应该是/app
WORKDIR /app

# 运行FS类
CMD ["java", "FS.FS"]
