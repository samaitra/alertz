#!/bin/sh
# Usage: create_fk-alert-service_deb env (local/production) 
die()
{
        echo "Error: $1" >&2
        exit 1
}

PACKAGE=fk-alert-service
CODE_ROOT="./alert-service"
PACKAGE_ROOT="./fk-alert-service"
VERSION=0.0.15
ARCH=all

COMPILE_DEPS="sun-java6-jre"
GIT_REPO="git@github.com:Flipkart/fk-alert-service.git"

[ -z "$1" ] && die "Target enviroment not specified"

if [ "$1" = "local" -o "$1" = "production" ]; then
  TARGET=$1
  echo "Building for" $TARGET
else
  die "Target is not a known environment"
fi

if dpkg -l $COMPILE_DEPS; then
  ##remove existing directories
  rm -rf $PACKAGE_ROOT
  rm -rf $CODE_ROOT
  rm fk-alert-service_*.deb
  ls
  
  git clone $GIT_REPO $CODE_ROOT/

  ## copy the configs specific to the environment
  cp $CODE_ROOT/config/$TARGET/* $CODE_ROOT/src/main/resources

  #####COMPILE HERE
  cd $CODE_ROOT
  mvn clean compile package  -Dmaven.test.skip=true
  cd - 

  #mv $PACKAGE_ROOT/config/fk-alert-service.yml $PACKAGE_ROOT/etc/fk-alert-service/config/fk-alert-service.yml
  mkdir -p $PACKAGE_ROOT/etc/$PACKAGE
  mkdir -p $PACKAGE_ROOT/DEBIAN
  mkdir -p $PACKAGE_ROOT/usr/share/$PACKAGE/lib
  mkdir -p $PACKAGE_ROOT/usr/share/$PACKAGE/app
  mkdir -p $PACKAGE_ROOT/usr/share/$PACKAGE/db
  mkdir -p $PACKAGE_ROOT/etc/init.d/
  
  cp $CODE_ROOT/config/fk-alert-service.yml $PACKAGE_ROOT/etc/$PACKAGE/fk-alert-service.yml
  cp $PACKAGE.control $PACKAGE_ROOT/DEBIAN/control
  cp $PACKAGE.postinst $PACKAGE_ROOT/DEBIAN/postinst
  cp $PACKAGE.postrm $PACKAGE_ROOT/DEBIAN/postrm
  cp $PACKAGE.preinst $PACKAGE_ROOT/DEBIAN/preinst
  cp $PACKAGE.prerm $PACKAGE_ROOT/DEBIAN/prerm
  cp $PACKAGE.init $PACKAGE_ROOT/etc/init.d/$PACKAGE

  #### COPY CODE BASE
  cp -R $CODE_ROOT/target/*.jar $PACKAGE_ROOT/usr/share/$PACKAGE/app/
  cp -R $CODE_ROOT/target/lib/*.jar $PACKAGE_ROOT/usr/share/$PACKAGE/lib/
  cp -R $CODE_ROOT/db/* $PACKAGE_ROOT/usr/share/$PACKAGE/db/
  
  sed -i "s/<VERSION>/$VERSION/g" $PACKAGE_ROOT/DEBIAN/control
  dpkg-deb -b $PACKAGE_ROOT
  mv $PACKAGE_ROOT.deb ${PACKAGE_ROOT}_${VERSION}_${ARCH}.deb
else
  echo "Please run apt-get install $COMPILE_DEPS"
  exit -1
fi
