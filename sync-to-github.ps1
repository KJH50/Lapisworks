# Lapisworks 同步脚本
# 用于将代码同步到 GitHub 仓库并触发自动构建

# 配置
$GitHubRepo = "KJH50/Lapisworks"
$WorkingDir = "E:\文件\工作\Lapisworks-build"

# 颜色输出
function Write-Step { param($msg) Write-Host "[步骤] $msg" -ForegroundColor Cyan }
function Write-Success { param($msg) Write-Host "[成功] $msg" -ForegroundColor Green }
function Write-Error { param($msg) Write-Host "[错误] $msg" -ForegroundColor Red }
function Write-Info { param($msg) Write-Host "[信息] $msg" -ForegroundColor Yellow }

# 检查 Git
Write-Step "检查 Git 安装..."
try {
    git --version | Out-Null
} catch {
    Write-Error "Git 未安装或未配置"
    exit 1
}

# 检查网络连接
Write-Step "检查 GitHub 连接..."
try {
    $null = Invoke-WebRequest -Uri "https://github.com" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
} catch {
    Write-Error "无法连接到 GitHub，请检查网络"
    exit 1
}

# 进入工作目录
Set-Location $WorkingDir

# 配置 Git
Write-Step "配置 Git..."
git config --global user.email "action@github.com"
git config --global user.name "GitHub Action"

# 检查远程仓库
Write-Step "检查远程仓库..."
$remotes = git remote -v
if ($remotes -notmatch "github.com/$GitHubRepo") {
    Write-Info "添加远程仓库..."
    git remote add origin "https://github.com/$GitHubRepo.git"
}

# 拉取最新代码
Write-Step "拉取最新代码..."
git pull origin main --rebase --allow-unrelated-histories

# 复制修改的文件
Write-Step "同步修改的代码..."
$sourceDir = "E:\文件\工作\Lapisworks"
if (Test-Path $sourceDir) {
    # 复制 AGENTS.md
    if (Test-Path "$sourceDir\AGENTS.md") {
        Copy-Item -Path "$sourceDir\AGENTS.md" -Destination "$WorkingDir\AGENTS.md" -Force
        Write-Info "已复制 AGENTS.md"
    }
}

# 显示更改
Write-Step "查看更改..."
git status

# 添加所有更改
Write-Step "添加更改..."
git add -A

# 检查是否有更改
$changes = git diff --cached --stat
if ([string]::IsNullOrEmpty($changes)) {
    Write-Info "没有新的更改需要提交"
} else {
    Write-Info "更改内容: $changes"
    
    # 提交
    Write-Step "提交更改..."
    $commitMsg = "Fix: 修复 AttributeMap 兼容性问题 | $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    git commit -m $commitMsg
    
    # 推送
    Write-Step "推送到 GitHub..."
    git push origin main
    
    Write-Success "代码已成功同步到 GitHub!"
    Write-Info "GitHub Actions 将自动开始构建..."
    Write-Info "查看构建状态: https://github.com/$GitHubRepo/actions"
}

Write-Host ""
Write-Success "操作完成!"
