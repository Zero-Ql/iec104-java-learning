@echo off
setlocal enabledelayedexpansion

:: 配置区域
set "headerFile=gpl-header.txt"
set "gplKeyword=GNU General Public License"
set "processedCount=0"

:: 检查头声明文件是否存在
if not exist "%headerFile%" (
    echo [错误] 未找到 %headerFile% 文件
    echo 请确保 gpl-header.txt 与脚本在同一目录
    pause
    exit /b 1
)

echo ======================================
echo IEC 104 GPL头声明添加工具
echo 处理目录: %CD%
echo ======================================

:: 递归处理所有.java文件
for /r %%f in (*.java) do (
    :: 检查是否已包含GPL声明
    findstr /c:"%gplKeyword%" "%%f" >nul 2>&1
    if !errorlevel! equ 0 (
        echo [跳过] 已存在: %%f
    ) else (
        echo [添加] %%f
        
        :: 创建临时文件并合并
        set "tempFile=%%f.tmp"
        type "%headerFile%" > "!tempFile!"
        type "%%f" >> "!tempFile!"
        move /y "!tempFile!" "%%f" >nul
        
        set /a "processedCount+=1"
    )
)

echo ======================================
echo 处理完成！共添加 %processedCount% 个文件
echo ======================================
pause