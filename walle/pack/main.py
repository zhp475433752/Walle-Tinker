# -*-coding:utf-8-*-
import os
import sys
import Config
import platform
import shutil


# 这里是获取encoding，一般是utf-8
if sys.stdout.encoding != 'UTF-8':
    print('sys.stdout.encoding != UTF-8')
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
if sys.stderr.encoding != 'UTF-8':
    print('sys.stderr.encoding != UTF-8')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')
print('walle---sys.stdout.encoding-----' + sys.stdout.encoding)
print('walle---sys.stdout.encoding-----' + sys.stderr.encoding)

# 获取脚本文件的当前路径
def curFileDir():
    # 获取脚本路径
    path = sys.path[0]
    # 判断为脚本文件还是py2exe编译后的文件，
    # 如果是脚本文件，则返回的是脚本的目录，
    # 如果是编译后的文件，则返回的是编译后的文件路径
    if os.path.isdir(path):
        return path
    elif os.path.isfile(path):
        return os.path.dirname(path)


# 兼容不同系统的路径分隔符
def getBackslash():
    # 判断当前系统
    if 'windows' in platform.system().lower():
        return "\\"
    else:
        return "/"


# 当前脚本文件所在目录
parentPath = curFileDir() + getBackslash()
print('walle---parentPath当前脚本文件所在目录-----' + parentPath)

# 这里是获取lib的路径，主要是获取lib目录下的文件路径，包含签名和瓦力路径
libPath = parentPath + "lib" + getBackslash()
print('walle---libPath-----' + libPath)
buildToolsPath = Config.sdkBuildToolPath + getBackslash()
print('walle---buildToolsPath-----' + buildToolsPath)
# 获取lib下签名jar的路径
checkAndroidV2SignaturePath = libPath + "CheckAndroidV2Signature.jar"
print('walle---获取lib下签名jar的路径-----' + checkAndroidV2SignaturePath)
# 获取lib下瓦力打包jar的路径
walleChannelPath = libPath + "walle-cli-all.jar"
print('walle---获取lib下瓦力打包jar的路径-----' + walleChannelPath)

# 这里是自定义输入路径
channelsOutputFilePath = parentPath + "output"
print('walle---channelsOutputFilePath-----' + channelsOutputFilePath)
# 这里是获取多渠道打包配置信息
channelFilePath = parentPath + "apk" + getBackslash() + "channel"
print('walle---channelFilePath-----' + channelFilePath)
# 这里是获取加固后源文件的路径
protectedSourceApkPath = parentPath + "apk" + getBackslash() + Config.protectedSourceApkName
print('walle---protectedSourceApkPath-----' + protectedSourceApkPath)
if os.access(protectedSourceApkPath, os.F_OK):
    print('walle---protectedSourceApkPath---apk存在--' + protectedSourceApkPath)
else:
    print('walle---protectedSourceApkPath---apk不存在--请确认根目录下的apk文件夹中是否有apk')
    exit(0)


def copyFile(srcFile, dstFile):
    shutil.copyfile(srcFile, dstFile)  # 复制文件


# 定义签名apk路径，如果文件不是_aligned.apk后缀名
if protectedSourceApkPath.find("_aligned.apk"):
    print('walle---protectedSourceApkPath-----' + protectedSourceApkPath)
    zipalignedApkPath = protectedSourceApkPath
    signedApkPath = zipalignedApkPath[0: -4] + "_signed.apk"
    copyFile(zipalignedApkPath, signedApkPath)
else:
    zipalignedApkPath = protectedSourceApkPath[0: -4] + "_aligned.apk"
    print('walle---zipalignedApkPath-----' + zipalignedApkPath)
    copyFile(protectedSourceApkPath, zipalignedApkPath)
    signedApkPath = zipalignedApkPath[0: -4] + "_signed.apk"
    print('walle---signedApkPath-----' + signedApkPath)
    copyFile(zipalignedApkPath, signedApkPath)


# 清空临时资源
def cleanTempResource():
    try:
        # os.remove(zipalignedApkPath)
        os.remove(signedApkPath)
        pass
    except Exception as e:
        # 如果异常则打印日志
        print('异常-清空临时资源-')
        print(e)
        pass


# 清空渠道信息
def cleanChannelsFiles():
    try:
        os.remove(channelsOutputFilePath)
        pass
    except Exception as e:
        # 如果异常则打印日志
        print('异常-清空渠道信息-')
        print(e)
        pass


# 创建Channels输出文件夹
def createChannelsDir():
    try:
        os.makedirs(channelsOutputFilePath)
        pass
    except Exception as e:
        print('异常-创建Channels输出文件夹-')
        print(e)
        pass


# 清除所有output文件夹下的文件
def delFile(path):
    ls = os.listdir(path)
    for i in ls:
        c_path = os.path.join(path, i)
        if os.path.isdir(c_path):
            del_file(c_path)
        else:
            os.remove(c_path)


# 先清除之前output文件夹中所有的文件
if len(Config.channelsOutputFilePath) > 0:
    delFile(channelsOutputFilePath)
# 清空Channels输出文件夹
# cleanChannelsFiles()
# 创建Channels输出文件夹
createChannelsDir()


# 对齐
zipalignShell = buildToolsPath + "zipalign -v 4 " + protectedSourceApkPath + " " + zipalignedApkPath
print('walle---zipalignShell-----' + zipalignShell)
os.system(zipalignShell)

# 签名 这里没有重新签名的过程
# signShell = buildToolsPath + "apksigner sign --ks " + keystorePath + " --ks-key-alias " \
#             + keyAlias + " --ks-pass pass:" + keystorePassword + " --key-pass pass:" \
#             + keyPassword + " --out " + signedApkPath + " " + zipalignedApkPath
# print('walle---signShell-----' + signShell)
# os.system(signShell)

# 检查V2签名是否正确
checkV2Shell = "java -jar " + checkAndroidV2SignaturePath + " " + signedApkPath
# print('walle---checkV2Shell-----' + signShell)
os.system(checkV2Shell)

# 写入渠道
if len(Config.extraChannelFilePath) > 0:
    writeChannelShell = "java -jar " + walleChannelPath + " batch2 -f " \
                        + Config.extraChannelFilePath + " " + signedApkPath + " " + channelsOutputFilePath
else:
    writeChannelShell = "java -jar " + walleChannelPath + " batch -f " + channelFilePath + " " \
                        + signedApkPath + " " + channelsOutputFilePath
print('walle---writeChannelShell-----' + writeChannelShell)
os.system(writeChannelShell)

# 清空临时资源
cleanTempResource()

print("\n**** =============================执行完成=================================== ****\n")
print("↓↓↓↓↓↓↓↓  Please check output in the path   ↓↓↓↓↓↓↓↓")
print("" + channelsOutputFilePath + "")
print("\n**** =============================执行完成=================================== ****\n")
