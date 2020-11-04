#### Windows 构建
```bash
# 打成jar包
$ mvn clean package

# 到jar包所在目录
$ cd target

# 生成exe程序并带jre（注意：更多使用请使用介绍：javapackager -help，^ 表示换行，如果是linux请使用 \）
# -deploy            打包构建（注意：命令的选项包括如下）
## -native           打包成当前系统所使用的包（所支持类型的列表包括: installer,image,exe,msi,dmg,rpm,deb）（推荐使用: image）
## -outdir           打包输出目录
## -name             打包输出文件夹名称（注意：这个文件夹在打包输出目录里面）
## -outfile          打包后程序入口文件名
## -srcfiles         要打包的jar文件所在目录
## -appclass         入口程序Main所在类名（注意：这个是全路径，就是 包名+类名）
$ javapackager -deploy                                                                                          ^
  -native image                                                                                                 ^
  -outdir D:\netpx-client_builder_dir                                                                           ^
  -outfile netpx-client                                                                                         ^
  -srcfiles E:\java-workspace-2019-09\netpx-client\target\netpx-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar ^
  -appclass org.firecode.netpx.client.Application                                                               ^
  -name netpx-client                                                                                   
```