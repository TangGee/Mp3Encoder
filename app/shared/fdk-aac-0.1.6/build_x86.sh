SYSROOT="$NDK/platforms/android-21/arch-x86_64"



export PATH=/tmp/x86_64-android-toolchain/bin:$PATH
export CC=x86_64-linux-android-gcc   # or export CC=clang
export CXX=x86_64-linux-android-g++  # or export CXX=clang++

MYPATH=`pwd`

./configure --host=x86_64 \
--disable-shared \
--enable-static \
--prefix=$MYPATH/x86_64

make clean
make -j8
make install
