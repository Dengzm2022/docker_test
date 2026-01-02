# Gallery 项目云服务器部署指南

本指南将协助您将 Gallery 图库项目部署到 Linux 云服务器（如阿里云、腾讯云、AWS 等）。

## 1. 准备工作

### 1.1 获取云服务器
确保您拥有一个 Linux 服务器（推荐 Ubuntu 20.04/22.04 或 CentOS 7/8）。
确保开启以下防火墙端口：
- **80**: 前端 Web 访问
- **8080**: 后端 API
- **9090**: Minio 控制台
- **9000**: Minio API
- **3306**: MySQL (仅调试用，生产环境建议不开放)

### 1.2 安装 Docker 环境

**第一步：检查服务器系统版本**
在终端输入：
```bash
cat /etc/os-release
```
- 如果显示 `NAME="Ubuntu"` 或 `Debian` -> 请使用 **apt** 命令。
- 如果显示 `NAME="CentOS"` 或 `Alibaba Cloud Linux` -> 请使用 **yum** 命令。

**方案 A：如果是 Ubuntu/Debian (推荐使用官方脚本，自动处理冲突)**
```bash
# 1. 卸载旧版本或冲突包
sudo apt-get remove -y docker docker-engine docker.io containerd runc

# 2. 使用官方脚本一键安装
curl -fsSL https://get.docker.com | sh

# 3. 启动并设置开机自启
sudo systemctl enable --now docker

# 4. 安装 Docker Compose (插件版已包含在上述脚本中，验证即可)
docker compose version
```

**方案 B：如果是 CentOS/RedHat/Alibaba Cloud**
```bash
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl enable --now docker
# 安装独立 docker-compose (如果 yum 源版本太老)
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## 2. 上传项目代码


### 方法一：使用 Git 拉取 (推荐)

**1. 安装 Git**
```bash
# Ubuntu/Debian
sudo apt install -y git

# CentOS
sudo yum install -y git
```

**2. 拉取代码**
假设您的代码托管在 GitHub/Gitee/GitLab：
```bash
# 创建目录
mkdir -p /opt/gallery
cd /opt/gallery

# 克隆仓库 (请替换为您实际的仓库地址)
# 如果是私有仓库，需要输入账号密码或使用 Token
git clone https://github.com/your-username/gallery-project.git .
```

**3. 更新代码 (后续发布)**
```bash
cd /opt/gallery
git pull
# 然后重新构建并重启服务
docker-compose up -d --build
```

### 方法二：手动上传 (SCP/SFTP)
如果您没有 Git 仓库，可以使用 WinSCP 或 FileZilla 将本地文件上传到 `/opt/gallery`。

需要包含的核心文件/目录：
- `backend/`
- `frontend/`

- `sql/` (数据库初始化脚本)
- `docker-compose.yml` (编排文件)

## 3. 修改配置 (可选)

如果需要修改数据库密码或 Minio 密钥，请编辑 `docker-compose.yml` 和 `backend/src/main/resources/application.yml`。
**注意**：`application.yml` 中的 MySQL 地址在 Docker 部署模式下应设为 `mysql` (Docker 服务名)，而不是 `127.0.0.1` 或公网 IP。

**在云端部署前，建议将 backend/src/main/resources/application.yml 恢复为 Docker 内部网络配置：**
```yaml
url: jdbc:mysql://mysql:3306/gallery...
minio.endpoint: http://minio:9000
```
*(当前本地环境为了调试改为了公网IP，部署时请改回服务名)*

## 4. 启动服务

进入项目目录并启动：

```bash
cd /opt/gallery
sudo docker-compose up -d --build
```

等待构建完成（初次构建可能需要几分钟下载镜像和编译）。

## 5. 验证部署

- **前端访问**：浏览器打开 `http://<服务器公网IP>`，应该能看到“Cute Gallery”界面。
- **Minio 控制台**：`http://<服务器公网IP>:9090` (默认账号密码: `minioadmin` / `minioadmin`)。
- **Portainer (可选)**：如果需要可视化管理 Docker，运行：
  ```bash
  docker run -d -p 9005:9000 --name portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer
  ```
  访问 `http://<服务器公网IP>:9005`。


## 6. 独立部署 Minio 服务 (可选)

如果您不想使用 docker-compose 一键启动，或者需要在其他服务器单独运行 Minio，请使用以下命令：

```bash
docker run -d \
  -p 9000:9000 \
  -p 9090:9090 \
  --name gallery-minio \
  --restart=always \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v minio_data:/data \
  minio/minio server /data --console-address ":9090"
```
**注意**：如果是独立部署，请确保后端服务的配置 (`application.yml`) 中 `minio.endpoint` 指向正确的 Minio 服务器 IP 地址。

## 7. Jenkins CI/CD 自动化 (进阶)

如果需要实现“代码提交即部署”，请在服务器上安装 Jenkins：

1. **运行 Jenkins 容器**：
   ```bash
   docker run -d -p 8081:8080 -p 50000:50000 --name jenkins --restart=always -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker jenkins/jenkins:lts
   ```
2. **配置流水线**：
   - 登录 Jenkins (`http://<IP>:8081`)，安装推荐插件。
   - 新建 Pipeline 任务，关联 Git 仓库。
   - 指向项目中的 `backend/Jenkinsfile` 或 `frontend/Jenkinsfile`。

---

**常见问题排查：**
- **无法访问页面？** 检查云厂商的安全组/防火墙是否放行了 80 端口。
- **后端报错连不上数据库？** 检查 `application.yml` 是否使用了 `mysql` 作为主机名，且 Docker 容器是否在同一网络（docker-compose 会自动处理）。
