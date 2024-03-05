FROM openjdk:11-jdk
WORKDIR /app

# 从当前目录复制US文件夹的内容到容器内的/app/US目录
COPY . /app/US

# 在容器内的/app/US目录下编译US.java文件
WORKDIR /app/US
RUN javac US.java

# 运行US类时，工作目录应该是/app
WORKDIR /app

# 运行US类
CMD ["java", "US.US"]