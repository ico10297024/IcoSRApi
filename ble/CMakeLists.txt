# 指定cmake最低版本，如果不知道就复制吧
cmake_minimum_required(VERSION 3.4.1)

# 第一个native-lib是供java调用的文件， SHARED是啥没看明白但是要写上
# 最后俩是所有cpp的名字和相对本文件的位置，这里在一个文件夹中就直接写名字了，文件有多少写多少
add_library(native-lib
SHARED
src/main/cpp/native-lib.cpp)

# 我的理解是java要连接到native-lib，和一些其它的东西，不懂
target_link_libraries(native-lib)