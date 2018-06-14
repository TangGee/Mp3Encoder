
export PATH=/tmp/x86_64-android-toolchain/bin:$PATH
export CC=x86_64-linux-android-gcc   # or export CC=clang
export CXX=x86_64-linux-android-g++  # or export CXX=clang++
export RANLIB="x86_64-linux-android-ranlib"
export STRIP="x86_64-linux-android-strip"
export AS=x86_64-linux-android-ar
export LD=x86_64-linux-android-ld
export NM=x86_64-linux-android-NM
export AR=x86_64-linux-android-ar

MYPATH=`pwd`

./configure --host=x86_64 \
--disable-shared \
--disable-frontend \
--enable-static \
--prefix=$MYPATH/x86_64

make clean
make -j8
make install
